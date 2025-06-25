package com.springboot.work.word.repository;

import com.springboot.work.word.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Word findByWordIgnoreCase(String word);
    List<Word> findByIsFavoriTrue();

}
