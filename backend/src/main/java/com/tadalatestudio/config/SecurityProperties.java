package com.tadalatestudio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

    private Jwt jwt = new Jwt();
    private List<String> allowedOrigins = List.of("http://localhost:3000");
    private int maxLoginAttempts = 5;
    private int accountLockDurationMinutes = 15;
    private PasswordPolicy passwordPolicy = new PasswordPolicy();

    @Data
    public static class Jwt {
        private String secret;
        private long expirationMs = 86400000; // 24 hours
        private long refreshExpirationMs = 604800000; // 7 days
    }

    @Data
    public static class PasswordPolicy {
        private int minLength = 8;
        private boolean requireUppercase = true;
        private boolean requireLowercase = true;
        private boolean requireDigit = true;
        private boolean requireSpecialChar = true;
        private int maxHistoryCount = 5;
    }
}
