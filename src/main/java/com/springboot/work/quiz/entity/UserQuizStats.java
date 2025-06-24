package com.springboot.work.quiz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "user_quiz_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
