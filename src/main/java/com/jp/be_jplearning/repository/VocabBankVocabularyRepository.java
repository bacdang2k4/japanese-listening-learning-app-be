package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.VocabBankVocabulary;
import com.jp.be_jplearning.entity.VocabBankVocabularyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabBankVocabularyRepository extends JpaRepository<VocabBankVocabulary, VocabBankVocabularyId> {
    List<VocabBankVocabulary> findByVocabBankIdOrderByVocabOrderAsc(Long vocabBankId);
}
