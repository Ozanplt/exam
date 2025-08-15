package org.example.service;

import org.example.repository.UserRepository;
import org.example.util.JwtUtil;

public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwt;

    public AuthService(UserRepository userRepository, JwtUtil jwt) {
        this.userRepository = userRepository;
        this.jwt = jwt;
    }

    public String login(String username, String password) {
        var user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("Invalid credentials");
        String hash = org.example.util.Password.hash(password);
        if (!hash.equals(user.getPasswordHash()))
            throw new RuntimeException("Invalid credentials");
        return jwt.createToken(user.getId(), user.getUsername());
    }

    public int authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new org.example.http.BaseHandler.UnauthorizedError("Missing bearer token");
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        var claims = jwt.verify(token);
        return (int) claims.userId();
    }
}
