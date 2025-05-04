package com.tadalatestudio.service;

import com.tadalatestudio.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.email.from:noreply@ticketbooking.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Async
    public void sendPasswordResetEmail(User user, String otp, int expiryMinutes) {
        try {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("name", user.getFirstName());
            templateModel.put("otp", otp);
            templateModel.put("expiryMinutes", expiryMinutes);

            sendEmail(user.getEmail(), "Password Reset Request", "password-reset", templateModel);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Async
    public void sendRegistrationConfirmationEmail(User user) {
        try {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("name", user.getFirstName());
            templateModel.put("loginUrl", frontendUrl + "/login");

            sendEmail(user.getEmail(), "Welcome to Event Ticket Booking", "registration-confirmation", templateModel);
            log.info("Registration confirmation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send registration confirmation email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String templateName, Map<String, Object> templateModel) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        Context context = new Context(Locale.getDefault());
        context.setVariables(templateModel);

        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String generateBarcodeUrl(String barcode) {
        // In a real application, you would generate an actual barcode image
        // For this example, we'll return a placeholder URL
        return frontendUrl + "/api/barcode/" + barcode;
    }

}
