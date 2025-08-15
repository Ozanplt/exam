package org.example.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Password {
    public static String hash(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e){ throw new RuntimeException("hash error"); }
    }
}
