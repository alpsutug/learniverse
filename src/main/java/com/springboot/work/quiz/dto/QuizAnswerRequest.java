package com.springboot.work.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizAnswerRequest {
    private List<Answer> answers;
    private String userEmail;

    @Data
    public static class Answer {
        private String english;      // Soru: İngilizce kelime
        private String selected;     // Kullanıcının seçtiği Türkçe anlam
    }
}
