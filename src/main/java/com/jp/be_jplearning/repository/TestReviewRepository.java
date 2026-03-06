package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.TestReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestReviewRepository extends JpaRepository<TestReview, Long> {
}
