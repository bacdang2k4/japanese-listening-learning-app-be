package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.VocabBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabBankRepository extends JpaRepository<VocabBank, Long> {
    List<VocabBank> findByTopicId(Long topicId);
}
