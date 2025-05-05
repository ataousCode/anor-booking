package com.tadalatestudio.service;

import com.tadalatestudio.dto.PasswordResetDTO;
import com.tadalatestudio.dto.PasswordResetRequestDTO;
import com.tadalatestudio.dto.UserDTO;
import com.tadalatestudio.dto.UserUpdateDTO;
import com.tadalatestudio.exception.BadRequestException;
import com.tadalatestudio.exception.ResourceNotFoundException;
import com.tadalatestudio.model.Role;
import com.tadalatestudio.model.User;
import com.tadalatestudio.repository.UserRepository;
import com.tadalatestudio.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final OtpUtil otpUtil;

    private static final String OTP_PREFIX = "password_reset_otp:";
    private static final int OTP_EXPIRY_MINUTES = 10;

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = getUserByItsId(id);
        return mapToDTO(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userUpdateDTO.getFirstName() != null) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null) {
            user.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getPhone() != null) {
            user.setPhone(userUpdateDTO.getPhone());
        }

        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequestDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requestDTO.getEmail()));

        String otp = otpUtil.generateOtp();
        String redisKey = OTP_PREFIX + user.getEmail();

        // Store OTP in Redis with expiration
        redisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);

        // Send email with OTP
        emailService.sendPasswordResetEmail(user, otp, OTP_EXPIRY_MINUTES);

        log.info("Password reset requested for user: {}", user.getEmail());
    }

    @Transactional
    public void resetPassword(PasswordResetDTO passwordResetDTO) {
        //validate OTP
        String redisKey = OTP_PREFIX + passwordResetDTO.getEmail();
        String storeOTP = redisTemplate.opsForValue().get(redisKey);

        if (storeOTP == null || !storeOTP.equals(passwordResetDTO.getOtp()))
            throw new BadRequestException("Invalid or expired OTP");

        // update password
        User user = userRepository.findByEmail(passwordResetDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + passwordResetDTO.getEmail() + " not found"));

        user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        userRepository.save(user);

        //delete OTP from redis
        redisTemplate.delete(redisKey);

        log.info("Password reset successful for user: {}", user.getEmail());
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserByItsId(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword()))
            throw new BadRequestException("Current password is incorrect");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getEmail());
    }

    @Transactional
    public void updateUserRole(Long id, Set<Role> roles) {
        User user = getUserByItsId(id);
        user.setRoles(roles);
        userRepository.save(user);

        log.info("Role updated for user: {} to {}", user.getEmail(), roles);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User getUserByItsId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }
}
