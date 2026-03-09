package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VocabularyResponse {
    private Long id;
    private String word;
    private String kana;
    private String romaji;
    private String meaning;
    private String exampleSentence;
    private LocalDateTime createdAt;
}
