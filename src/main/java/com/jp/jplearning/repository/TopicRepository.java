package com.jp.jplearning.repository;

import com.jp.jplearning.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic> findByLevel_LevelId(Integer levelId);
}
