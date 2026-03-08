package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.AudioTest;
import com.jp.be_jplearning.entity.enums.TestStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AudioTestRepository extends JpaRepository<AudioTest, Long> {
        Page<AudioTest> findByTopicIdAndStatus(Long topicId, TestStatusEnum status, Pageable pageable);

        @Query("SELECT a FROM AudioTest a WHERE " +
                        "(:topicId IS NULL OR a.topic.id = :topicId) AND " +
                        "(CAST(:status AS string) IS NULL OR a.status = :status) AND " +
                        "(:keyword IS NULL OR LOWER(a.testName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
        Page<AudioTest> searchAudioTests(
                        @Param("topicId") Long topicId,
                        @Param("status") TestStatusEnum status,
                        @Param("keyword") String keyword,
                        Pageable pageable);
}
