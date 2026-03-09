package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    @Query("SELECT v FROM Vocabulary v WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(v.word) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(v.kana) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(v.romaji) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(v.meaning) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Vocabulary> searchVocabularies(@Param("keyword") String keyword, Pageable pageable);
}
