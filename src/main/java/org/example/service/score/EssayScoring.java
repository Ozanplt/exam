package org.example.service.score;

import org.example.model.EssayQuestion;
import org.example.model.Question;
import org.example.model.QuestionType;

public class EssayScoring implements ScoringStrategy {
    @Override
    public int score(Question q, String answer) {
        if (q.getType() != QuestionType.ESSAY) return 0;
        if (answer == null || answer.isBlank()) return 0;
        var es = (EssayQuestion) q;
        var expected = es.getExpectedKeywords();
        if (expected == null || expected.isBlank()) return 0;
        int hits = 0;
        for (String kw : expected.split(",")) {
            if (answer.toLowerCase().contains(kw.trim().toLowerCase())) hits++;
        }
        return hits > 0 ? 1 : 0;
    }
}
