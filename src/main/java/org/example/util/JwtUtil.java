package org.example.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtUtil {
    private final byte[] secret;

    public record Claims(long userId, String username, long exp) {}

    public JwtUtil(String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String createToken(int userId, String username) {
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + 3600; // 1 saat
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64Url(String.format("{\"sub\":%d,\"usr\":\"%s\",\"exp\":%d}", userId, escape(username), exp));
        String unsigned = header + "." + payload;
        String sig = sign(unsigned);
        return unsigned + "." + sig;
    }

    public Claims verify(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new RuntimeException("Invalid token");
        String unsigned = parts[0] + "." + parts[1];
        String expectedSig = sign(unsigned);
        if (!constantTimeEq(expectedSig, parts[2])) throw new RuntimeException("Bad signature");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        long userId = getLong(payloadJson, "\"sub\":");
        String username = getString(payloadJson, "\"usr\":\"");
        long exp = getLong(payloadJson, "\"exp\":");
        long now = System.currentTimeMillis() / 1000L;
        if (now > exp) throw new RuntimeException("Token expired");
        return new Claims(userId, username, exp);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        } catch (Exception e) { throw new RuntimeException("HMAC error"); }
    }

    private String base64Url(String s){
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private boolean constantTimeEq(String a, String b){
        if (a.length()!=b.length()) return false;
        int r=0; for(int i=0;i<a.length();i++) r |= a.charAt(i)^b.charAt(i); return r==0;
    }

    private static String escape(String s){ return s.replace("\"","\\\""); }

    private static long getLong(String json, String key){
        int i = json.indexOf(key); if (i<0) throw new RuntimeException("Malformed token");
        i += key.length();
        int j = json.indexOf(",", i); if (j<0) j = json.indexOf("}", i);
        return Long.parseLong(json.substring(i, j).trim());
    }

    private static String getString(String json, String keyWithQuote){
        int i = json.indexOf(keyWithQuote); if (i<0) return "";
        i += keyWithQuote.length();
        int j = json.indexOf("\"", i);
        return json.substring(i, j);
    }
}
