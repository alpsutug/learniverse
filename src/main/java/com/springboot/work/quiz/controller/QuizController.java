package com.springboot.work.quiz.controller;

import com.springboot.work.quiz.dto.QuizAnswerRequest;
import com.springboot.work.quiz.dto.QuizQuestion;
import com.springboot.work.quiz.dto.QuizResultResponse;
import com.springboot.work.quiz.entity.QuizResult;
import com.springboot.work.quiz.repository.QuizResultRepository;
import com.springboot.work.quiz.service.impl.QuizServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizServiceImpl quizServiceImpl;
    private final QuizResultRepository quizResultRepository;

    @GetMapping("/generateAll")
    public List<QuizQuestion> getQuiz(
            @RequestParam String level, @RequestParam String category, @RequestParam(defaultValue = "10") int count) {
        return quizServiceImpl.generateQuiz(level, category, count);
    }

    @PostMapping("/check")
    public QuizResultResponse checkAnswers(@RequestBody QuizAnswerRequest request) {
        return quizServiceImpl.checkAnswers(request);
    }
    // eğer jsonda olmayaan bir şey body de gönderilirse direkt 40 3 dönüyr düzgün hata dönmeli
    // ve qcorrect olayı düzelitlmeli serviste bahsettim
    //insanlar giriş yapmak zorunda değil diyrcekek mailsiz bu servise istek atamyı da kabul edeceğiz çünkü db ye üdşüyor ama aksi ise sadece mail ile istek atılabilecek hale glmeli bu servis


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getQuizStats(@RequestParam String email) {
        return ResponseEntity.ok(quizServiceImpl.getQuizStats(email));
    }

    // yeni bir tablo oluşturulup bu değerleri orada tutmalıyız totalleri

    @GetMapping("/mixed")
    public List<QuizQuestion> getMixedQuiz(@RequestParam String level, @RequestParam(defaultValue = "10") int count, @RequestParam(required = false) List<String> categories) {
        return quizServiceImpl.mixCategory(level, categories, count);
    }

    @GetMapping("/random")
    public List<QuizQuestion> getRandomQuiz(@RequestParam(defaultValue = "10") int count) {
        return quizServiceImpl.randomMixedQuiz(count);
    }

    @GetMapping("/daily-quota")
    public ResponseEntity<Map<String, Object>> getDailyQuota(@RequestParam String email, @RequestParam(defaultValue = "10") int target) {

        int remaining = quizServiceImpl.getRemainingDailyQuota(email, target);
        Map<String, Object> result = new HashMap<>();
        result.put("target", target);
        result.put("remaining", remaining);
        return ResponseEntity.ok(result);
    }


}
