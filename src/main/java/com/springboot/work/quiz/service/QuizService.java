package com.springboot.work.quiz.service;

import com.springboot.work.quiz.dto.QuizAnswerRequest;
import com.springboot.work.quiz.dto.QuizQuestion;
import com.springboot.work.quiz.dto.QuizResultResponse;
import com.springboot.work.word.entity.Word;

import java.util.List;
import java.util.Map;

public interface QuizService {
    List<QuizQuestion> generateQuiz(String level, String category, int questionCount);
    QuizResultResponse checkAnswers(QuizAnswerRequest request);
    Map<String, Object> getQuizStats(String email);
    List<QuizQuestion> mixCategory(String level, List<String> categories, int count);
    QuizQuestion toQuizQuestion(Word correctWord);
    List<QuizQuestion> randomMixedQuiz(int count);
}
