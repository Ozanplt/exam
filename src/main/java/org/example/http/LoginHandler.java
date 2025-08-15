package org.example.http;

import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
import org.example.service.AuthService;
import org.example.util.ValidationUtil;
import com.sun.net.httpserver.HttpExchange;

public class LoginHandler extends BaseHandler {
    private final AuthService authService;

    public LoginHandler(AuthService authService) { this.authService = authService; }

    @Override
    protected void doHandle(HttpExchange exchange) throws Exception {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod()))
            throw new ClientError("Method not allowed");

        var req = readJson(exchange, LoginRequest.class);
        ValidationUtil.requireNonBlank(req.username(), "username");
        ValidationUtil.requireNonBlank(req.password(), "password");

        var token = authService.login(req.username(), req.password());
        sendJson(exchange, 200, new LoginResponse(token, "Bearer"));
    }
}
