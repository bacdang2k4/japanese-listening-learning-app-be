package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.AudioTest;
import com.jp.be_jplearning.entity.enums.TestStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioTestRepository extends JpaRepository<AudioTest, Long> {
    Page<AudioTest> findByTopicIdAndStatus(Long topicId, TestStatusEnum status, Pageable pageable);
}
