package com.springboot.work.quiz.service.impl;

import com.springboot.work.quiz.dto.QuizAnswerRequest;
import com.springboot.work.quiz.dto.QuizResultResponse;
import com.springboot.work.quiz.dto.QuizQuestion;
import com.springboot.work.quiz.entity.QuizResult;
import com.springboot.work.quiz.repository.QuizResultRepository;
import com.springboot.work.quiz.service.QuizService;
import com.springboot.work.word.entity.Word;
import com.springboot.work.word.service.impl.WordServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final WordServiceImpl wordServiceImpl;
    private final QuizResultRepository quizResultRepository;

    @Override
    public List<QuizQuestion> generateQuiz(String level, String category, int questionCount) {
        List<Word> wordPool = wordServiceImpl.getByLevelAndCategory(level, category);
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

@Override
    public QuizResultResponse checkAnswers(QuizAnswerRequest request) {
        int correctCount = 0;
        List<QuizResultResponse.ResultDetail> detailList = new ArrayList<>();

        for (QuizAnswerRequest.Answer answer : request.getAnswers()) {
            Word word = wordServiceImpl.getWordByEnglish(answer.getEnglish());
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
            }
        }   // bu hata console da geliyor serviste 403 atıyor bunu da düzlet

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

@Override
    public List<QuizQuestion> mixCategory(String level,
                                          List<String> categories,
                                          int count) {

        /* 1) Havuzu oluştur */
        List<Word> pool = wordServiceImpl.getAllWords().stream()
                .filter(w -> w.getLevel().equalsIgnoreCase(level))               // seviye
                .filter(w -> categories == null || categories.isEmpty()          // kategori
                        || categories.stream().anyMatch(
                        c -> c.equalsIgnoreCase(
                                Optional.ofNullable(w.getCategory())
                                        .orElse("")
                        )))
                .collect(Collectors.toList());

        if (pool.isEmpty()) {
            throw new IllegalArgumentException(
                    "Bu seviye / kategorilerde yeterli kelime yok!");
        }

        /* 2) Rastgele <count> kelime seç */
        Collections.shuffle(pool);
        List<Word> selected = pool.subList(0, Math.min(count, pool.size()));

        /* 3) QuizQuestion’a dönüştür */
        return selected.stream()
                .map(this::toQuizQuestion)
                .toList();
    }

    /* ------------------------------------------------------ */

    /**
     * Word → QuizQuestion çevirisi (tek doğru + 3 yanlış şık üretir)
     */
    @Override
    public QuizQuestion toQuizQuestion(Word correctWord) {

        // aynı seviyeden rastgele 3 yanlış anlam
        List<String> wrongPool = wordServiceImpl.getByLevel(correctWord.getLevel())
                .stream()
                .filter(w -> !w.getMeaning()
                        .equalsIgnoreCase(
                                correctWord.getMeaning()))
                .map(Word::getMeaning)
                .collect(Collectors.toList());
        Collections.shuffle(wrongPool);

        Set<String> options = new HashSet<>();
        options.add(correctWord.getMeaning());
        options.addAll(wrongPool.subList(0,
                Math.min(3, wrongPool.size())));   // maks. 3 yanlış

        List<String> shuffled = new ArrayList<>(options);
        Collections.shuffle(shuffled);

        return new QuizQuestion(
                correctWord.getWord(),         // soru (İngilizce)
                shuffled,                      // şıklar
                correctWord.getMeaning());     // doğru cevap
    }

    @Override
    public List<QuizQuestion> randomMixedQuiz(int count) {

        // 1) Tüm kelimeleri çek
        List<Word> pool = wordServiceImpl.getAllWords();   // JSON’dan gelen liste

        // 2) Karıştır
        Collections.shuffle(pool);

        // 3) İstenen sayıda kelime seç
        List<Word> selected = pool.subList(0, Math.min(count, pool.size()));

        // 4) QuizQuestion’a dönüştür
        return selected.stream()
                .map(this::toQuizQuestion)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getQuizStats(String email) {
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

}
