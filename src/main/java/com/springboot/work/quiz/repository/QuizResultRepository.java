package com.springboot.work.quiz.repository;

import com.springboot.work.quiz.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserEmail(String email);
}
