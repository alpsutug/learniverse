package com.springboot.work.quiz.service;

import com.springboot.work.quiz.dto.QuizAnswerRequest;
import com.springboot.work.quiz.dto.QuizResultResponse;
import com.springboot.work.quiz.dto.QuizQuestion;
import com.springboot.work.quiz.entity.QuizResult;
import com.springboot.work.quiz.repository.QuizResultRepository;
import com.springboot.work.word.entity.Word;
import com.springboot.work.word.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final WordService wordService;
    private final QuizResultRepository quizResultRepository;

    public List<QuizQuestion> generateQuiz(String level, String category, int questionCount) {
        List<Word> wordPool = wordService.getByLevelAndCategory(level, category);
        Collections.shuffle(wordPool);

        List<QuizQuestion> quizQuestions = new ArrayList<>();

        for (int i = 0; i < Math.min(questionCount, wordPool.size()); i++) {
            Word correctWord = wordPool.get(i);

            Set<String> options = new HashSet<>();
            options.add(correctWord.getMeaning()); // Doğru cevap

            Random random = new Random();
            while (options.size() < 4) {
                Word randomWord = wordPool.get(random.nextInt(wordPool.size()));
                options.add(randomWord.getMeaning()); // Yanlış cevapları ekle
            }

            List<String> shuffledOptions = new ArrayList<>(options);
            Collections.shuffle(shuffledOptions);

            QuizQuestion question = new QuizQuestion();
            question.setQuestion(correctWord.getWord()); // İngilizce kelime
            question.setOptions(shuffledOptions);
            question.setCorrectAnswer(correctWord.getMeaning()); // Türkçesi

            quizQuestions.add(question);
        }

        return quizQuestions;
    }



    public QuizResultResponse checkAnswers(QuizAnswerRequest request) {
        int correctCount = 0;
        List<QuizResultResponse.ResultDetail> detailList = new ArrayList<>();

        for (QuizAnswerRequest.Answer answer : request.getAnswers()) {
            Word word = wordService.getWordByEnglish(answer.getEnglish());
            boolean isCorrect = word.getMeaning().equalsIgnoreCase(answer.getSelected());

            if (isCorrect) correctCount++;

            QuizResultResponse.ResultDetail detail = new QuizResultResponse.ResultDetail();
            detail.setEnglish(word.getWord());
            detail.setQCorrect(word.getMeaning());//correctler karışıyordu
            detail.setSelected(answer.getSelected());
            detail.setCorrect(isCorrect);

            detailList.add(detail);
        }


        int total = request.getAnswers().size();
        int correct = correctCount;
        int wrong = total - correct;

        String feedback;
        double score = (double) correct / total;
        if (score == 1.0) {
            feedback = "🥳 Mükemmel! Tüm cevaplar doğru!";
        } else if (score >= 0.75) {
            feedback = "👍 Harika gidiyorsun!";
        } else if (score >= 0.5) {
            feedback = "💡 Biraz daha pratik yap!";
        } else {
            feedback = "😓 Daha çok çalışman gerek gibi görünüyor.";
        }

        QuizResultResponse result = new QuizResultResponse();
        result.setTotalQuestions(total);
        result.setCorrectAnswers(correct);
        result.setWrongAnswers(wrong);
        result.setFeedback(feedback);
        result.setDetails(detailList);

        Set<String> seen = new HashSet<>();
        for (QuizAnswerRequest.Answer answer : request.getAnswers()) {
            if (!seen.add(answer.getEnglish())) {
                throw new IllegalArgumentException("Aynı kelime birden fazla kez gönderilemez: " + answer.getEnglish());
            }}   // bu hata console da geliyor serviste 403 atıyor bunu da düzlet

        QuizResult quizResult = new QuizResult();
        quizResult.setUserEmail(request.getUserEmail());
        quizResult.setTotalQuestions(request.getAnswers().size());
        quizResult.setCorrectAnswers(correctCount);
        quizResult.setWrongAnswers(wrong);
        quizResult.setSuccessRate((double) correct / total); // ✅ oran (0.0 - 1.0 arası)
        quizResult.setTimestamp(LocalDateTime.now());

        quizResultRepository.save(quizResult);

        return result;
    }



}
