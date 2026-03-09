package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.AIGenerationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIGenerationLogRepository extends JpaRepository<AIGenerationLog, Long> {
    Page<AIGenerationLog> findAllByOrderByGeneratedAtDesc(Pageable pageable);
}
