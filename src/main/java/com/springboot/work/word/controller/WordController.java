package com.springboot.work.word.controller;

import com.springboot.work.word.entity.Word;
import com.springboot.work.word.service.impl.WordServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/words")
@RequiredArgsConstructor
public class WordController {

    private final WordServiceImpl wordServiceImpl;

    @GetMapping
    public List<Word> getAllWords() {
        return wordServiceImpl.getAllWords();
    }

    @GetMapping("/level/{level}")
    public List<Word> getByLevel(@PathVariable String level) {
        return wordServiceImpl.getByLevel(level);
    }

    @GetMapping("/category/{category}")
    public List<Word> getByCategory(@PathVariable String category) {
        return wordServiceImpl.getByCategory(category);
    }

    @GetMapping("/filter")
    public List<Word> getByLevelAndCategory(@RequestParam String level, @RequestParam String category) {
        return wordServiceImpl.getByLevelAndCategory(level, category);
    }

    @GetMapping("/quiz")
    public List<Word> getRandomWords(
            @RequestParam String level,
            @RequestParam(defaultValue = "10") int count) {

        return wordServiceImpl.getRandomWordsByLevel(level, count);
    }
}
