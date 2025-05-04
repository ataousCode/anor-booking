package com.tadalatestudio.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
public class AuditLogger {

    public void logSecurityEvent(String eventType, String username, String details) {
        String ipAddress = getClientIpAddress();
        String userAgent = getUserAgent();

        log.info("SECURITY_EVENT: type={}, user={}, ip={}, userAgent={}, time={}, details={}",
                eventType, username, ipAddress, userAgent, LocalDateTime.now(), details);
    }

    public void logSuccessfulAuthentication(String username) {
        logSecurityEvent("AUTHENTICATION_SUCCESS", username, "User successfully authenticated");
    }

    public void logFailedAuthentication(String username, String reason) {
        logSecurityEvent("AUTHENTICATION_FAILURE", username, "Authentication failed: " + reason);
    }

    public void logAccountLockout(String username, String reason) {
        logSecurityEvent("ACCOUNT_LOCKOUT", username, "Account locked: " + reason);
    }

    public void logAccessDenied(String username, String resource) {
        logSecurityEvent("ACCESS_DENIED", username, "Access denied to resource: " + resource);
    }

    private String getClientIpAddress() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(this::extractIpAddress)
                .orElse("unknown");
    }

    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getUserAgent() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(request -> request.getHeader("User-Agent"))
                .orElse("unknown");
    }
}

