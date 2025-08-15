package org.example.service;

import org.example.dto.*;
import org.example.model.*;
import org.example.repository.ExamRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.ResultRepository;
import org.example.service.score.ScoringStrategyFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ExamService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;

    private final ExamCache cache = new ExamCache(5 * 60_000);

    public ExamService(ExamRepository examRepository, QuestionRepository questionRepository, ResultRepository resultRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
    }

    public ExamDeliveryDTO getExamForDelivery(int examId, int userId) {
        var exam = cache.getOrPut(examId, () -> examRepository.findExam(examId));
        if (exam == null) throw new org.example.http.BaseHandler.NotFoundError("Exam not found: " + examId);

        var qDtos = exam.getQuestions().stream().map(q -> switch (q.getType()) {
            case MULTIPLE_CHOICE -> {
                var mc = (MultipleChoiceQuestion) q;
                yield new QuestionDTO(mc.getId(), mc.getType(), mc.getText(), mc.getOptions());
            }
            case ESSAY -> new QuestionDTO(q.getId(), q.getType(), q.getText(), List.of());
        }).collect(Collectors.toList());

        return new ExamDeliveryDTO(exam.getId(), exam.getTitle(), qDtos);
    }

    public Result submitAndScore(int userId, SubmitRequest req) {
        var exam = cache.getOrPut(req.examId(), () -> examRepository.findExam(req.examId()));
        if (exam == null) throw new org.example.http.BaseHandler.NotFoundError("Exam not found: " + req.examId());

        Map<Integer, String> answersMap = new HashMap<>();
        for (var a : req.answers()) answersMap.put(a.questionId(), a.answer());

        int score = 0, correct = 0, total = exam.getQuestions().size();
        for (var q : exam.getQuestions()) {
            var ans = answersMap.get(q.getId());
            int qScore = ScoringStrategyFactory.get(q.getType()).score(q, ans);
            score += qScore;
            if (qScore > 0) correct++;
        }

        var result = new Result(exam.getId(), userId, score, correct, total);
        resultRepository.save(result);
        return result;
    }
}
