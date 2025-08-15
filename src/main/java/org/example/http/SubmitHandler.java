package org.example.http;

import org.example.dto.SubmitRequest;
import org.example.dto.SubmitResponse;
import org.example.service.AuthService;
import org.example.service.ExamService;
import org.example.util.ValidationUtil;
import com.sun.net.httpserver.HttpExchange;

public class SubmitHandler extends BaseHandler {
    private final AuthService authService;
    private final ExamService examService;

    public SubmitHandler(AuthService authService, ExamService examService) {
        this.authService = authService;
        this.examService = examService;
    }

    @Override
    protected void doHandle(HttpExchange exchange) throws Exception {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod()))
            throw new ClientError("Method not allowed");

        var authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        var userId = authService.authenticate(authHeader);

        var req = readJson(exchange, SubmitRequest.class);
        ValidationUtil.requirePositive(req.examId(), "examId");
        ValidationUtil.requireNotNull(req.answers(), "answers");
        if (req.answers().isEmpty()) throw new ClientError("answers cannot be empty");

        var result = examService.submitAndScore(userId, req);
        sendJson(exchange, 200, new SubmitResponse(req.examId(), userId, result.score(), result.correctCount(), result.totalQuestions()));
    }
}
