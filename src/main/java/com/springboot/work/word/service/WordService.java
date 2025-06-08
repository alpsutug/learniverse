package com.springboot.work.word.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.work.word.entity.Word;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordService {

    private List<Word> allWords;

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
        return allWords;
    }

    public List<Word> getByLevel(String level) {
        return allWords.stream()
                .filter(w -> w.getLevel().equalsIgnoreCase(level))
                .collect(Collectors.toList());
    }

    public List<Word> getByCategory(String category) {
        return allWords.stream()
                .filter(w -> category.equalsIgnoreCase(w.getCategory()))
                .collect(Collectors.toList());
    }

    public List<Word> getByLevelAndCategory(String level, String category) {
        return allWords.stream()
                .filter(w -> w.getLevel().equalsIgnoreCase(level) && category.equalsIgnoreCase(w.getCategory()))
                .collect(Collectors.toList());
    }

    public Word getWordByEnglish(String word) {
        return allWords.stream()
                .filter(w -> w.getWord().equalsIgnoreCase(word))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Kelime bulunamadı: " + word));
    }
}
