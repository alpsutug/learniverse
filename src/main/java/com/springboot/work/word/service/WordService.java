package com.springboot.work.word.service;

import com.springboot.work.user.entity.Users;
import com.springboot.work.word.entity.Word;

import java.util.List;

public interface WordService {

    List<Word> getAllWords();
    List<Word> getByLevel(String level);
    List<Word> getByCategory(String category);
    List<Word> getByLevelAndCategory(String level, String category);
    Word getWordByEnglish(String word);
    List<Word> getRandomWordsByLevel(String level, int count);
    Word getWordName(String word);
    List<Word> getFavoriWords();

}
