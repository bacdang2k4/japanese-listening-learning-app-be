package com.jp.be_jplearning.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VocabBankResponse {
    private Long id;
    private String title;
    private String description;
    private Long topicId;
    private String topicName;
    private List<VocabBankVocabularyResponse> vocabularies;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class VocabBankVocabularyResponse {
        private Long vocabId;
        private String word;
        private String kana;
        private String romaji;
        private String meaning;
        private String exampleSentence;
        private Integer vocabOrder;
    }
}
