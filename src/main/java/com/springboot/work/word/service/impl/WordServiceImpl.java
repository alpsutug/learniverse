package com.springboot.work.word.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.work.user.dto.UserResponseDTO;
import com.springboot.work.user.entity.Users;
import com.springboot.work.util.WorkMessageDTO;
import com.springboot.work.util.WorkMessageType;
import com.springboot.work.word.dto.WordResponseDTO;
import com.springboot.work.word.entity.Word;
import com.springboot.work.word.repository.WordRepository;
import com.springboot.work.word.service.WordService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    private final OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor;
    private List<Word> allWords;
    private final WordRepository wordRepository;




    @PostConstruct
    public void init() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("data/words.json")) {
            ObjectMapper mapper = new ObjectMapper();
            allWords = mapper.readValue(is, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kelime verisi yüklenemedi!", e);
        }
    }

    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }

    @Override
    public List<Word> getByLevel(String level) {
        return wordRepository.findByLevelIgnoreCase(level);
    }


    @Override
    public List<Word> getByCategory(String category) {
        return wordRepository.findByCategoryIgnoreCase(category);
    }


    @Override
    public List<Word> getByLevelAndCategory(String level, String category) {
        return wordRepository.findByLevelIgnoreCaseAndCategoryIgnoreCase(level, category);
    }


    public Word getWordByEnglish(String word) {
        return allWords.stream()
                .filter(w -> w.getWord().equalsIgnoreCase(word))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Kelime bulunamadı: " + word));
    }



    /* ---------- SEVİYEYE GÖRE RASTGELE KELİME ---------- */
    public List<Word> getRandomWordsByLevel(String level, int count) {

        // 1) Belirtilen seviyedeki kelimeleri süz
        List<Word> pool = allWords.stream()
                .filter(w -> w.getLevel()
                        .equalsIgnoreCase(level))
                .collect(Collectors.toList());

        // 2) Karıştır
        Collections.shuffle(pool);

        // 3) İstenen sayıyı döndür  (eldeki kelime sayısından büyükse hepsini ver)
        return pool.subList(0, Math.min(count, pool.size()));
    }

    /* Diğer metotlar (getAllWords, getByLevel vs.) aynen kalabilir */

    @Override
    public Word getWordName(String word) {
        return wordRepository.findByWordIgnoreCase(word);
    }

    @Override
    public List<Word> getFavoriWords() {
        return wordRepository.findByIsFavoriTrue();
    }

    @Override
    public List<Word> getByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return wordRepository.findAll();
        }

        List<String> lowerCats = categories.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return wordRepository.findByCategoryListIgnoreCase(lowerCats);
    }




    @Override
    public WordResponseDTO addToFavori(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kelime bulunamadı, ID: " + id));

        word.setFavori(true);
        wordRepository.save(word);

        WorkMessageDTO successMessage = WorkMessageDTO.builder()
                .type(WorkMessageType.SUCCESS)
                .text("Kelime başarıyla favorilere eklendi.")
                .build();

        return WordResponseDTO.builder()
                .wordId(word.getId())
                .msg(List.of(successMessage))
                .build();
    }


}
