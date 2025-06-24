package com.springboot.work.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerRequest {
    private List<Answer> answers;
    private String userEmail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {
        private String english;      // Soru: İngilizce kelime
        private String selected;     // Kullanıcının seçtiği Türkçe anlam
    }
}
