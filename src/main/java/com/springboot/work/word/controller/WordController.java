package com.springboot.work.word.controller;

import com.springboot.work.user.entity.Users;
import com.springboot.work.word.entity.Word;
import com.springboot.work.word.service.WordService;
import com.springboot.work.word.service.impl.WordServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @GetMapping
    public List<Word> getAllWords() {
        return wordService.getAllWords();
    }

    @GetMapping("/level/{level}")
    public List<Word> getByLevel(@PathVariable String level) {
        return wordService.getByLevel(level);
    }

    @GetMapping("/category/{category}")
    public List<Word> getByCategory(@PathVariable String category) {
        return wordService.getByCategory(category);
    }

    @GetMapping("/filter")
    public List<Word> getByLevelAndCategory(@RequestParam String level, @RequestParam String category) {
        return wordService.getByLevelAndCategory(level, category);
    }

    @GetMapping("/quiz")
    public List<Word> getRandomWords(
            @RequestParam String level,
            @RequestParam(defaultValue = "10") int count) {

        return wordService.getRandomWordsByLevel(level, count);
    }

    @GetMapping(path = "/wordName")
    @ResponseStatus(HttpStatus.OK)
    public Word getWordName(@RequestParam String word) {
        return wordService.getWordName(word);

    }


    @GetMapping("/favori")
    public ResponseEntity<List<Word>> getFavoriWords() {
        return ResponseEntity.ok(wordService.getFavoriWords());
    }
}
