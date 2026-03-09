package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Learner;
import com.jp.be_jplearning.entity.enums.LearnerStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearnerRepository extends JpaRepository<Learner, Long> {
    Optional<Learner> findByUsername(String username);

    Optional<Learner> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT l FROM Learner l WHERE " +
            "(CAST(:status AS string) IS NULL OR l.status = :status) AND " +
            "(:keyword IS NULL OR " +
            "LOWER(l.username) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(l.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(l.firstName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(l.lastName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Learner> searchLearners(@Param("keyword") String keyword, @Param("status") LearnerStatusEnum status,
            Pageable pageable);
}
