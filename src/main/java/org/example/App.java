package org.example;

import org.example.http.ExamsHandler;
import org.example.http.LoginHandler;
import org.example.http.StaticFileHandler;
import org.example.http.SubmitHandler;
import org.example.repository.JdbcExamRepository;
import org.example.repository.JdbcQuestionRepository;
import org.example.repository.JdbcResultRepository;
import org.example.repository.JdbcUserRepository;
import org.example.service.AuthService;
import org.example.service.ExamService;
import org.example.util.JwtUtil;
import org.example.util.Log;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {
        // Test için böyle yaptım, normalde env'den okunmalı
        String jdbcUrl = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/examdb?useSSL=false&allowPublicKeyRetrieval=true");
        String dbUser = System.getenv().getOrDefault("DB_USER", "root");
        String dbPass = System.getenv().getOrDefault("DB_PASS", "asdasd");
        String jwtSecret = System.getenv().getOrDefault("JWT_SECRET", "123123");

        var userRepo = new JdbcUserRepository(jdbcUrl, dbUser, dbPass);
        var examRepo = new JdbcExamRepository(jdbcUrl, dbUser, dbPass);
        var questionRepo = new JdbcQuestionRepository(jdbcUrl, dbUser, dbPass);
        var resultRepo = new JdbcResultRepository(jdbcUrl, dbUser, dbPass);

        var jwt = new JwtUtil(jwtSecret);
        var authService = new AuthService(userRepo, jwt);
        var examService = new ExamService(examRepo, questionRepo, resultRepo);

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/static", new StaticFileHandler("./public"));
        server.createContext("/login", new LoginHandler(authService));
        server.createContext("/exams", new ExamsHandler(authService, examService));
        server.createContext("/submit", new SubmitHandler(authService, examService));

        server.setExecutor(Executors.newFixedThreadPool(100));
        server.start();
        Log.INFO.log("Server started on http://localhost:8000");
    }
}
