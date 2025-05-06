package com.tadalatestudio.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.UUID;

@Component
public class BarcodeGenerator {

    private static final String BARCODE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BARCODE_LENGTH = 12;
    private final SecureRandom random = new SecureRandom();

    //generate a unique barcode for tickets
    public String generateBarcode() {
        String uuid = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 8);
        StringBuilder barcode = new StringBuilder(uuid);
        for (int i = 0; i < BARCODE_LENGTH - 8; i++) {
            barcode.append(BARCODE_CHARS.charAt(random.nextInt(BARCODE_CHARS.length())));
        }
        return barcode.toString();
    }
}
