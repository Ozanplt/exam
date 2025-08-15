package org.example.http;

import org.example.dto.ExamResponse;
import org.example.service.AuthService;
import org.example.service.ExamService;
import com.sun.net.httpserver.HttpExchange;

public class ExamsHandler extends BaseHandler {
    private final AuthService authService;
    private final ExamService examService;

    public ExamsHandler(AuthService authService, ExamService examService) {
        this.authService = authService;
        this.examService = examService;
    }

    @Override
    protected void doHandle(HttpExchange exchange) throws Exception {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()))
            throw new ClientError("Method not allowed");
        var authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        var userId = authService.authenticate(authHeader);

        var idStr = pathParamAfter(exchange, "/exams");
        if (idStr == null) throw new ClientError("Missing exam id");
        int examId;
        try { examId = Integer.parseInt(idStr); } catch (NumberFormatException e) { throw new ClientError("Invalid exam id"); }

        var examDto = examService.getExamForDelivery(examId, userId); // correctAnswer gizlenmiş DTO
        sendJson(exchange, 200, new ExamResponse(examDto.examId(), examDto.title(), examDto.questions()));
    }
}
