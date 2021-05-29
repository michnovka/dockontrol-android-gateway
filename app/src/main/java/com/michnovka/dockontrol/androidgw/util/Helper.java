package com.michnovka.dockontrol.androidgw.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Helper {

    public String toSHA256(String phoneNumber, long time, String api_secret) {
        String value = phoneNumber + "|" + time + "|" + api_secret;
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
