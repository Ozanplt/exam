package org.example.http;

import org.example.dto.ErrorResponse;
import org.example.util.JsonUtil;
import org.example.util.Log;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class BaseHandler implements HttpHandler {

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        try {
            doHandle(exchange);
        } catch (ClientError e) {
            // Beklenen/validasyon hataları
            Log.WARN.log("Client error: " + e.getMessage());
            sendJson(exchange, 400, new ErrorResponse("Bad Request", e.getMessage()));
        } catch (UnauthorizedError e) {
            Log.WARN.log("Unauthorized: " + e.getMessage());
            sendJson(exchange, 401, new ErrorResponse("Unauthorized", "Authentication required"));
        } catch (ForbiddenError e) {
            Log.WARN.log("Forbidden: " + e.getMessage());
            sendJson(exchange, 403, new ErrorResponse("Forbidden", "Not allowed"));
        } catch (NotFoundError e) {
            Log.INFO.log("Not Found: " + e.getMessage());
            sendJson(exchange, 404, new ErrorResponse("Not Found", e.getMessage()));
        } catch (Exception e) {
            // Beklenmeyen hatalar: stack trace loglanır, hassas veri yok.
            Log.ERROR.log("Unexpected error in handler", e);
            sendJson(exchange, 500, new ErrorResponse("Internal Server Error", "An unexpected error occurred"));
        } finally {
            exchange.close();
        }
    }

    protected abstract void doHandle(HttpExchange exchange) throws Exception;

    protected <T> T readJson(HttpExchange ex, Class<T> cls) throws IOException {
        try (InputStream is = ex.getRequestBody()) {
            return JsonUtil.fromJson(is, cls);
        } catch (Exception e) {
            throw new ClientError("Invalid JSON");
        }
    }

    protected void sendJson(HttpExchange ex, int status, Object body) throws IOException {
        byte[] json = JsonUtil.toJsonBytes(body);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, json.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(json);
        }
    }

    protected String pathParamAfter(HttpExchange ex, String base) {
        var path = ex.getRequestURI().getPath();
        if (!path.startsWith(base)) return null;
        var rest = path.substring(base.length());
        if (rest.startsWith("/")) rest = rest.substring(1);
        return rest.isBlank() ? null : rest;
    }

    public static class ClientError extends RuntimeException { public ClientError(String m){super(m);} }
    public static class UnauthorizedError extends RuntimeException { public UnauthorizedError(String m){super(m);} }
    public static class ForbiddenError extends RuntimeException { public ForbiddenError(String m){super(m);} }
    public static class NotFoundError extends RuntimeException { public NotFoundError(String m){super(m);} }
}
