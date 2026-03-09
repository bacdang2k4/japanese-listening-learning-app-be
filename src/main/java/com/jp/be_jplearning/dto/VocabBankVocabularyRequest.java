package com.jp.be_jplearning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VocabBankVocabularyRequest {
    @NotNull(message = "must not be null")
    private Long vocabId;

    @NotNull(message = "must not be null")
    private Integer vocabOrder;
}
