package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findByAdminId(Long adminId);
}
