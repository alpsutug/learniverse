package com.springboot.work.quiz.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizResultResponse {
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private String feedback;
    private List<ResultDetail> details;

    @Data
    public static class ResultDetail {
        private String english;
        private String qCorrect;
        private String selected;
        private boolean isCorrect;
    }
}
