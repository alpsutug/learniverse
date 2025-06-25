package com.springboot.work.word.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Table(name = "words")
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String meaning;
    private String level;     // A1, A2, B1...
    private String category;// sports, animals, etc (null olabilir)
    @Column(name = "is_favori")
    private boolean isFavori;
}
