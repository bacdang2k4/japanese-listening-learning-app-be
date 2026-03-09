package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.VocabBankVocabulary;
import com.jp.be_jplearning.entity.VocabBankVocabularyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabBankVocabularyRepository extends JpaRepository<VocabBankVocabulary, VocabBankVocabularyId> {
    List<VocabBankVocabulary> findByVocabBankIdOrderByVocabOrderAsc(Long vocabBankId);

    @Query("SELECT vbv FROM VocabBankVocabulary vbv " +
            "JOIN FETCH vbv.vocabulary " +
            "WHERE vbv.vocabBank.id IN :bankIds " +
            "ORDER BY vbv.vocabOrder ASC")
    List<VocabBankVocabulary> findByVocabBankIdsWithVocabulary(@Param("bankIds") List<Long> bankIds);
}
