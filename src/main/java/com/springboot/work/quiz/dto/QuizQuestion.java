package com.springboot.work.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizQuestion { //ismi değiş dto ekle ayrıca servic serviceimpl ekle hem word e hem buraya
    private String question;             // İngilizce kelime
    private List<String> options;        // 4 Türkçe şık
    private String correctAnswer;        // Doğru Türkçe karşılık
}
