package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.GeneratedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratedQuestionRepository extends JpaRepository<GeneratedQuestion, Long> {
}
