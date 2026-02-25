package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.LearnerAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearnerAnswerRepository extends JpaRepository<LearnerAnswer, Long> {
    List<LearnerAnswer> findByTestResultId(Long testResultId);
}
