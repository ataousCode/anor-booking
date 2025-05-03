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

    /**
     * Log a security event
     *
     * @param eventType the type of security event
     * @param username the username associated with the event
     * @param details additional details about the event
     */
    public void logSecurityEvent(String eventType, String username, String details) {
        String ipAddress = getClientIpAddress();
        String userAgent = getUserAgent();

        log.info("SECURITY_EVENT: type={}, user={}, ip={}, userAgent={}, time={}, details={}",
                eventType, username, ipAddress, userAgent, LocalDateTime.now(), details);
    }

    /**
     * Log a successful authentication
     *
     * @param username the username that was authenticated
     */
    public void logSuccessfulAuthentication(String username) {
        logSecurityEvent("AUTHENTICATION_SUCCESS", username, "User successfully authenticated");
    }

    /**
     * Log a failed authentication
     *
     * @param username the username that failed authentication
     * @param reason the reason for the failure
     */
    public void logFailedAuthentication(String username, String reason) {
        logSecurityEvent("AUTHENTICATION_FAILURE", username, "Authentication failed: " + reason);
    }

    /**
     * Log an account lockout
     *
     * @param username the username of the locked account
     * @param reason the reason for the lockout
     */
    public void logAccountLockout(String username, String reason) {
        logSecurityEvent("ACCOUNT_LOCKOUT", username, "Account locked: " + reason);
    }

    /**
     * Log an access denied event
     *
     * @param username the username that was denied access
     * @param resource the resource that was attempted to be accessed
     */
    public void logAccessDenied(String username, String resource) {
        logSecurityEvent("ACCESS_DENIED", username, "Access denied to resource: " + resource);
    }

    /**
     * Get the client IP address from the current request
     *
     * @return the client IP address or "unknown" if not available
     */
    private String getClientIpAddress() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(this::extractIpAddress)
                .orElse("unknown");
    }

    /**
     * Extract the IP address from the request, considering X-Forwarded-For headers
     *
     * @param request the HTTP request
     * @return the client IP address
     */
    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Get the user agent from the current request
     *
     * @return the user agent or "unknown" if not available
     */
    private String getUserAgent() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(request -> request.getHeader("User-Agent"))
                .orElse("unknown");
    }
}

