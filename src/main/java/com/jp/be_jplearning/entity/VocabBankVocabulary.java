package com.jp.be_jplearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vocabbank_vocabulary")
@Getter
@Setter
@NoArgsConstructor
public class VocabBankVocabulary {

    @EmbeddedId
    private VocabBankVocabularyId id = new VocabBankVocabularyId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vocabBankId")
    @JoinColumn(name = "vocab_bank_id")
    private VocabBank vocabBank;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("vocabId")
    @JoinColumn(name = "vocab_id")
    private Vocabulary vocabulary;

    @Column(name = "vocab_order")
    private Integer vocabOrder;

    public VocabBankVocabulary(VocabBank vocabBank, Vocabulary vocabulary, Integer vocabOrder) {
        this.vocabBank = vocabBank;
        this.vocabulary = vocabulary;
        this.vocabOrder = vocabOrder;
        this.id = new VocabBankVocabularyId(vocabBank.getId(), vocabulary.getId());
    }
}
