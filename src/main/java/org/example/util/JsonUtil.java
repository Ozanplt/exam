package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectMapper mapper(){ return MAPPER; }

    public static byte[] toJsonBytes(Object o){
        try { return MAPPER.writeValueAsBytes(o); }
        catch (Exception e){ throw new RuntimeException("JSON serialize error"); }
    }

    public static <T> T fromJson(InputStream is, Class<T> cls){
        try { return MAPPER.readValue(is, cls); }
        catch (Exception e){ throw new RuntimeException("JSON parse error"); }
    }
}
