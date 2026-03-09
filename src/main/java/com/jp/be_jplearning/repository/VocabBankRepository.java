package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.VocabBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabBankRepository extends JpaRepository<VocabBank, Long> {
    List<VocabBank> findByTopicId(Long topicId);

    @Query("SELECT vb FROM VocabBank vb WHERE " +
            "(:topicId IS NULL OR vb.topic.id = :topicId) AND " +
            "(:keyword IS NULL OR LOWER(vb.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<VocabBank> searchVocabBanks(@Param("topicId") Long topicId, @Param("keyword") String keyword,
            Pageable pageable);
}
