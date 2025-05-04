package com.tadalatestudio.service;

import com.tadalatestudio.config.PasswordValidator;
import com.tadalatestudio.config.SecurityProperties;
import com.tadalatestudio.jwt.JwtTokenProvider;
import com.tadalatestudio.repository.RefreshTokenRepository;
import com.tadalatestudio.repository.RoleRepository;
import com.tadalatestudio.repository.UserRepository;
import com.tadalatestudio.security.AuditLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogger auditLogger;
    private final SecurityProperties securityProperties;
}
