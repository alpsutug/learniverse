package com.springboot.work.word.entity;

import lombok.Data;

@Data
public class Word {
    private String word;
    private String meaning;
    private String level;     // A1, A2, B1...
    private String category;  // sports, animals, etc (null olabilir)
}
