package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VocabularyRequest {
    @NotBlank(message = "must not be blank")
    private String word;

    private String kana;

    private String romaji;

    private String meaning;

    private String exampleSentence;
}
