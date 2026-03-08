package com.jp.be_jplearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "vocabulary")
@Getter
@Setter
@NoArgsConstructor
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocab_id")
    private Long id;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "kana")
    private String kana;

    @Column(name = "romaji")
    private String romaji;

    @Column(name = "meaning", columnDefinition = "TEXT")
    private String meaning;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    private String exampleSentence;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
