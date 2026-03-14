package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.TestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Page<TestResult> findByAttempt_Profile_Id(Long profileId, Pageable pageable);

    @Query("SELECT tr FROM TestResult tr " +
            "JOIN tr.attempt a JOIN a.profile p JOIN p.learner l " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(l.username) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(l.firstName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(l.lastName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(a.test.testName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))) " +
            "AND (:passed IS NULL OR tr.isPassed = :passed)")
    Page<TestResult> searchTestResults(@Param("keyword") String keyword,
            @Param("passed") Boolean passed,
            Pageable pageable);

    @Query("SELECT COUNT(tr) FROM TestResult tr " +
            "WHERE tr.attempt.profile.id = :profileId " +
            "AND tr.attempt.test.topic.id = :topicId " +
            "AND tr.isPassed = true")
    long countPassedByProfileAndTopic(@Param("profileId") Long profileId,
            @Param("topicId") Long topicId);

    @Query("SELECT COUNT(tr) FROM TestResult tr " +
            "WHERE tr.attempt.profile.id = :profileId " +
            "AND tr.attempt.test.topic.id = :topicId")
    long countByProfileAndTopic(@Param("profileId") Long profileId,
            @Param("topicId") Long topicId);

    @Query("SELECT COALESCE(SUM(tr.score), 0) FROM TestResult tr " +
            "WHERE tr.attempt.profile.id = :profileId")
    int sumScoreByProfileId(@Param("profileId") Long profileId);
}
