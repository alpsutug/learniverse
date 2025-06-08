package com.springboot.work.quiz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class UserQuizStats {

    @Id
    private String email;

    private int totalQuestions;
    private int totalCorrect;
    private int totalWrong;

    public double getSuccessRate() {
        return totalQuestions == 0 ? 0.0 : ((double) totalCorrect / totalQuestions) * 100;
    }
}
