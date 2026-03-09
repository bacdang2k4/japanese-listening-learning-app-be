package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("SELECT t FROM Topic t WHERE " +
            "(:levelId IS NULL OR t.level.id = :levelId) AND " +
            "(:keyword IS NULL OR LOWER(t.topicName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Topic> searchTopics(@Param("levelId") Long levelId, @Param("keyword") String keyword, Pageable pageable);

    List<Topic> findByLevelId(Long levelId);
}
