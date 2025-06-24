package com.springboot.work.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private String feedback;
    private List<ResultDetail> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDetail {
        private String english;
        private String qCorrect;
        private String selected;
        private boolean isCorrect;
    }
}
