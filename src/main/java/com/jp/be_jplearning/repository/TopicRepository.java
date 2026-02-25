package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
