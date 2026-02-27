package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Learner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearnerRepository extends JpaRepository<Learner, Long> {
    Optional<Learner> findByUsername(String username);

    Optional<Learner> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
