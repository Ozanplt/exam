package org.example.http;

import org.example.util.Log;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFileHandler implements HttpHandler {
    private final Path root;

    public StaticFileHandler(String rootDir) {
        this.root = Path.of(rootDir).toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            String reqPath = ex.getRequestURI().getPath().replaceFirst("^/static/?", "");
            if (reqPath.isBlank()) reqPath = "index.html";
            Path file = root.resolve(reqPath).normalize();
            if (!file.startsWith(root) || !Files.exists(file) || Files.isDirectory(file)) {
                ex.sendResponseHeaders(404, -1);
                return;
            }
            String ct = contentType(file.getFileName().toString());
            byte[] bytes = Files.readAllBytes(file);
            ex.getResponseHeaders().add("Content-Type", ct);
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
        } catch (Exception e) {
            Log.ERROR.log("Static serve error", e);
            ex.sendResponseHeaders(500, -1);
        } finally { ex.close(); }
    }

    private String contentType(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".html") || n.endsWith(".htm")) return "text/html; charset=utf-8";
        if (n.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (n.endsWith(".css")) return "text/css; charset=utf-8";
        if (n.endsWith(".json")) return "application/json; charset=utf-8";
        if (n.endsWith(".png")) return "image/png";
        if (n.endsWith(".jpg") || n.endsWith(".jpeg")) return "image/jpeg";
        if (n.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
