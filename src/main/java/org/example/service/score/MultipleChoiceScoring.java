package org.example.service.score;

import org.example.model.MultipleChoiceQuestion;
import org.example.model.Question;
import org.example.model.QuestionType;

public class MultipleChoiceScoring implements ScoringStrategy {
    @Override
    public int score(Question q, String answer) {
        if (q.getType() != QuestionType.MULTIPLE_CHOICE) return 0;
        if (answer == null) return 0;
        var mc = (MultipleChoiceQuestion) q;
        return answer.equals(mc.getCorrectOption()) ? 1 : 0;
    }
}
