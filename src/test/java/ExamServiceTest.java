
import org.example.dto.AnswerDTO;
import org.example.dto.SubmitRequest;
import org.example.model.*;
import org.example.repository.ExamRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.ResultRepository;
import org.example.service.ExamService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExamServiceTest {

    static class FakeExamRepo implements ExamRepository {
        private final Exam exam;
        FakeExamRepo(Exam exam){ this.exam = exam; }
        @Override public Exam findExam(int examId) { return exam; }
    }
    static class FakeQRepo implements QuestionRepository {
        private final List<Question> qs;
        FakeQRepo(List<Question> qs){ this.qs = qs; }
        @Override public List<Question> findByExamId(int examId) { return qs; }
    }
    static class FakeResultRepo implements ResultRepository {
        Result last;
        @Override public void save(Result result) { last = result; }
    }

    @Test
    void calculateScore_MixedQuestions() {
        var q1 = new MultipleChoiceQuestion(101, 5, "MC Q1", List.of("A","B","C","D"), "C");
        var q2 = new EssayQuestion(102, 5, "ESSAY Q2", "oop,polymorphism");
        var exam = new Exam(5, "Java", List.of(q1, q2));

        var svc = new ExamService(new FakeExamRepo(exam), new FakeQRepo(exam.getQuestions()), new FakeResultRepo());
        var req = new SubmitRequest(5, List.of(
                new AnswerDTO(101, "C"),
                new AnswerDTO(102, "Polymorphism in OOP")
        ));
        var result = svc.submitAndScore(12, req);

        assertEquals(2, result.score());
        assertEquals(2, result.correctCount());
        assertEquals(2, result.totalQuestions());
    }
}
