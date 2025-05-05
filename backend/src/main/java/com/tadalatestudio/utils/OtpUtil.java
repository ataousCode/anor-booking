package com.tadalatestudio.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpUtil {

    private static final String OTP_CHARS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private final SecureRandom secureRandom = new SecureRandom();

    //generate random otp
    public String generateOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++)
            otp.append(OTP_CHARS.charAt(secureRandom.nextInt(OTP_CHARS.length())));
        return otp.toString();
    }
}
