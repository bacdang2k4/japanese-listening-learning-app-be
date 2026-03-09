package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.TestResult;
import com.jp.be_jplearning.entity.enums.TestModeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Page<TestResult> findByAttempt_Profile_Id(Long profileId, Pageable pageable);

    @Query("SELECT COUNT(tr) FROM TestResult tr " +
            "WHERE tr.attempt.profile.id = :profileId " +
            "AND tr.attempt.test.topic.id = :topicId " +
            "AND tr.attempt.mode = :mode " +
            "AND tr.isPassed = true")
    long countPassedByProfileAndTopicAndMode(@Param("profileId") Long profileId,
            @Param("topicId") Long topicId,
            @Param("mode") TestModeEnum mode);

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
}
