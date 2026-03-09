package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.LearnerAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LearnerAnswerRepository extends JpaRepository<LearnerAnswer, Long> {
    List<LearnerAnswer> findByAttemptId(Long attemptId);

    @Query("SELECT la FROM LearnerAnswer la " +
            "JOIN FETCH la.question " +
            "LEFT JOIN FETCH la.selectedAnswer " +
            "WHERE la.attempt.id = :attemptId")
    List<LearnerAnswer> findByAttemptIdWithDetails(@Param("attemptId") Long attemptId);
}
