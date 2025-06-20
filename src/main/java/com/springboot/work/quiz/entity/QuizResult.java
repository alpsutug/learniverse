package com.springboot.work.quiz.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers; // ✅ yeni alan
    private double successRate; // ✅ yeni alan: 0.75 gibi

    private LocalDateTime timestamp;
}
