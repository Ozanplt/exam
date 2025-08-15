package org.example.service.score;

import org.example.model.QuestionType;

public final class ScoringStrategyFactory {
    private static final ScoringStrategy MC = new MultipleChoiceScoring();
    private static final ScoringStrategy ES = new EssayScoring();

    public static ScoringStrategy get(QuestionType type) {
        return switch (type) {
            case MULTIPLE_CHOICE -> MC;
            case ESSAY -> ES;
        };
    }
}
