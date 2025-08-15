package org.example.util;

public final class ValidationUtil {
    public static void requireNonBlank(String v, String field){
        if (v == null || v.isBlank()) throw new org.example.http.BaseHandler.ClientError(field + " cannot be blank");
    }
    public static void requirePositive(int v, String field){
        if (v <= 0) throw new org.example.http.BaseHandler.ClientError(field + " must be positive");
    }
    public static void requireNotNull(Object v, String field){
        if (v == null) throw new org.example.http.BaseHandler.ClientError(field + " cannot be null");
    }
}
