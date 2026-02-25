package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.TestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Page<TestResult> findByProfileTopic_Id_ProfileId(Long profileId, Pageable pageable);
}
