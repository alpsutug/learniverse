package com.springboot.work.quiz.controller;

import com.springboot.work.quiz.dto.QuizAnswerRequest;
import com.springboot.work.quiz.dto.QuizQuestion;
import com.springboot.work.quiz.dto.QuizResultResponse;
import com.springboot.work.quiz.entity.QuizResult;
import com.springboot.work.quiz.repository.QuizResultRepository;
import com.springboot.work.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuizResultRepository quizResultRepository;

    @GetMapping
    public List<QuizQuestion> getQuiz(
            @RequestParam String level,
            @RequestParam String category,
            @RequestParam(defaultValue = "10") int count
    ) {
        return quizService.generateQuiz(level, category, count);
    }

    @PostMapping("/check")
    public QuizResultResponse checkAnswers(@RequestBody QuizAnswerRequest request) {
        return quizService.checkAnswers(request);
    }
    // eğer jsonda olmayaan bir şey body de gönderilirse direkt 40 3 dönüyr düzgün hata dönmeli
    // ve qcorrect olayı düzelitlmeli serviste bahsettim
    //insanlar giriş yapmak zorunda değil diyrcekek mailsiz bu servise istek atamyı da kabul edeceğiz çünkü db ye üdşüyor ama aksi ise sadece mail ile istek atılabilecek hale glmeli bu servis




    @GetMapping("/stats")
    public Map<String, Object> getQuizStats(@RequestParam String email) {
        List<QuizResult> results = quizResultRepository.findByUserEmail(email);

        int totalQuizCount = results.size();
        int totalCorrect = results.stream().mapToInt(QuizResult::getCorrectAnswers).sum();
        int totalQuestions = results.stream().mapToInt(QuizResult::getTotalQuestions).sum();

        double successRate = totalQuestions > 0 ? ((double) totalCorrect / totalQuestions) * 100 : 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("quizCount", totalQuizCount);
        response.put("totalQuestions", totalQuestions);
        response.put("totalCorrect", totalCorrect);
        response.put("successRate", String.format("%.2f", successRate) + " %");

        return response;
    }

    // yeni bir tablo oluşturulup bu değerleri orada tutmalıyız totalleri

    @GetMapping("/mixed")
    public List<QuizQuestion> getMixedQuiz(
            @RequestParam String level,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) List<String> categories) {

        return quizService.mixCategory(level, categories, count);
    }

    @GetMapping("/random")
    public List<QuizQuestion> getRandomQuiz(
            @RequestParam(defaultValue = "10") int count) {

        return quizService.randomMixedQuiz(count);
    }


}
