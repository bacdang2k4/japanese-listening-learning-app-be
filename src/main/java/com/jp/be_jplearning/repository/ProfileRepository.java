package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByLearnerId(Long learnerId);

    @Query("SELECT p FROM Profile p JOIN p.learner l WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(l.username) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(l.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(l.firstName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) OR " +
            "LOWER(l.lastName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Profile> searchProfiles(@Param("keyword") String keyword, Pageable pageable);
}
