package com.tadalatestudio.service;

import com.tadalatestudio.config.PasswordValidator;
import com.tadalatestudio.config.SecurityProperties;
import com.tadalatestudio.dto.AuthResponseDTO;
import com.tadalatestudio.dto.LoginRequestDTO;
import com.tadalatestudio.dto.RegisterRequestDTO;
import com.tadalatestudio.exception.BadRequestException;
import com.tadalatestudio.exception.ResourceNotFoundException;
import com.tadalatestudio.exception.UnauthorizedException;
import com.tadalatestudio.jwt.JwtTokenProvider;
import com.tadalatestudio.model.RefreshToken;
import com.tadalatestudio.model.Role;
import com.tadalatestudio.model.User;
import com.tadalatestudio.repository.RefreshTokenRepository;
import com.tadalatestudio.repository.RoleRepository;
import com.tadalatestudio.repository.UserRepository;
import com.tadalatestudio.security.AuditLogger;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogger auditLogger;
    private final SecurityProperties securityProperties;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            auditLogger.logSecurityEvent("REGISTRATION_FAILURE", registerRequest.getEmail(), "Email already taken");
            throw new BadRequestException("Email is already taken");
        }

        // Validate password
        passwordValidator.validatePassword(registerRequest.getPassword());

        // Get or create default user role
        Role userRole = roleService.getRoleByName(Role.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // Create user
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
                .roles(roles)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        auditLogger.logSecurityEvent("REGISTRATION_SUCCESS", savedUser.getEmail(), "User registered successfully");

        // Send welcome email
        emailService.sendRegistrationConfirmationEmail(savedUser);

        // Authenticate the new user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = createRefreshToken(savedUser);

        return new AuthResponseDTO(
                accessToken,
                refreshToken,
                savedUser.getId(),
                savedUser.getEmail(),
                getUserRoleNames(savedUser)
        );
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

            // Reset failed attempts on successful login
            if (user.getFailedAttempt() > 0) {
                user.setFailedAttempt(0);
                user.setLockTime(null);
                userRepository.save(user);
            }

            // Generate tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = createRefreshToken(user);

            auditLogger.logSuccessfulAuthentication(user.getEmail());

            // Extract role names safely from the user object
            Set<String> roleNames = new HashSet<>();
            if (user.getRoles() != null) {
                // Create a defensive copy to avoid ConcurrentModificationException
                new HashSet<>(user.getRoles()).forEach(role -> roleNames.add(role.getName()));
            }

            return new AuthResponseDTO(
                    accessToken,
                    refreshToken,
                    user.getId(),
                    user.getEmail(),
                    roleNames
            );
        } catch (BadCredentialsException e) {
            // Handle failed login attempt safely
            try {
                Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    if (user.isAccountNonLocked()) {
                        if (user.getFailedAttempt() < securityProperties.getMaxLoginAttempts() - 1) {
                            user.setFailedAttempt(user.getFailedAttempt() + 1);
                            userRepository.save(user);
                        } else {
                            user.setFailedAttempt(user.getFailedAttempt() + 1);
                            user.setAccountNonLocked(false);
                            user.setLockTime(LocalDateTime.now());
                            userRepository.save(user);

                            auditLogger.logAccountLockout(user.getEmail(), "Maximum failed login attempts exceeded");
                            throw new LockedException("Your account has been locked due to " +
                                    securityProperties.getMaxLoginAttempts() +
                                    " failed login attempts. It will be unlocked after " +
                                    securityProperties.getAccountLockDurationMinutes() + " minutes.");
                        }
                    }
                }
            } catch (Exception ex) {
                // If any exception occurs while handling the bad credentials, log it but don't interrupt the flow
                // This prevents a secondary exception from masking the original BadCredentialsException
                log.error("Error handling failed login attempt: {}", ex.getMessage());
            }

            auditLogger.logFailedAuthentication(loginRequest.getEmail(), "Invalid credentials");
            throw e;
        }
    }


    /**
     * Refresh an access token using a refresh token
     *
     * @param refreshTokenStr the refresh token
     * @return authentication response with new JWT token
     * @throws UnauthorizedException if refresh token is invalid or expired
     */
    @Transactional
    public AuthResponseDTO refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> {
                    auditLogger.logSecurityEvent("TOKEN_REFRESH_FAILURE", "unknown", "Invalid refresh token");
                    return new UnauthorizedException("Invalid refresh token");
                });

        // Check if token is expired
        if (refreshToken.getExpiryAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            auditLogger.logSecurityEvent("TOKEN_REFRESH_FAILURE", refreshToken.getUser().getEmail(), "Expired refresh token");
            throw new UnauthorizedException("Refresh token was expired");
        }

        User user = refreshToken.getUser();

        // Create authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, user.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                .collect(java.util.stream.Collectors.toList()));

        // Generate new tokens
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = createRefreshToken(user);

        // Delete old refresh token
        refreshTokenRepository.delete(refreshToken);

        auditLogger.logSecurityEvent("TOKEN_REFRESH_SUCCESS", user.getEmail(), "Token refreshed successfully");

        return new AuthResponseDTO(
                accessToken,
                newRefreshToken,
                user.getId(),
                user.getEmail(),
                getUserRoleNames(user)
        );
    }

    /**
     * Logout a user by invalidating their refresh token
     *
     * @param token the JWT token
     */
    @Transactional
    public void logout(String token) {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));

            // Delete all refresh tokens for the user
            refreshTokenRepository.deleteAllByUserId(user.getId());

            auditLogger.logSecurityEvent("LOGOUT", username, "User logged out successfully");
        } catch (Exception e) {
            log.error("Error during logout", e);
        }
    }

    /**
     * Create a refresh token for a user
     *
     * @param user the user
     * @return the refresh token string
     */
    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryAt(LocalDateTime.now().plusSeconds(securityProperties.getJwt().getRefreshExpirationMs() / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    /**
     * Get role names for a user
     *
     * @param user the user
     * @return set of role names
     */
    private Set<String> getUserRoleNames(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(java.util.stream.Collectors.toSet());
    }


}
