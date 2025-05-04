package com.tadalatestudio.config;

import com.tadalatestudio.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PasswordValidator {
    private final SecurityProperties securityProperties;

    /**
     * Validate a password against the configured password policy
     *
     * @param password the password to validate
     * @throws BadRequestException if the password does not meet the policy requirements
     */
    public void validatePassword(String password) {
        List<String> validationErrors = new ArrayList<>();
        SecurityProperties.PasswordPolicy policy = securityProperties.getPasswordPolicy();

        if (password.length() < policy.getMinLength()) {
            validationErrors.add("Password must be at least " + policy.getMinLength() + " characters long");
        }

        if (policy.isRequireUppercase() && !Pattern.compile("[A-Z]").matcher(password).find()) {
            validationErrors.add("Password must contain at least one uppercase letter");
        }

        if (policy.isRequireLowercase() && !Pattern.compile("[a-z]").matcher(password).find()) {
            validationErrors.add("Password must contain at least one lowercase letter");
        }

        if (policy.isRequireDigit() && !Pattern.compile("\\d").matcher(password).find()) {
            validationErrors.add("Password must contain at least one digit");
        }

        if (policy.isRequireSpecialChar() && !Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            validationErrors.add("Password must contain at least one special character");
        }

        if (!validationErrors.isEmpty()) {
            throw new BadRequestException("Password validation failed: " + String.join(", ", validationErrors));
        }
    }
}
