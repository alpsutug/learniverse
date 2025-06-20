package com.springboot.work.quiz.repository;

import com.springboot.work.quiz.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserEmail(String userEmail);
    Optional<QuizResult> findTopByUserEmailOrderByTimestampDesc(String userEmail);

}
