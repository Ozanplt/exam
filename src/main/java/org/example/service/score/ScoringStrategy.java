package org.example.service.score;

import org.example.model.Question;

public interface ScoringStrategy {
    int score(Question q, String answer);
}
