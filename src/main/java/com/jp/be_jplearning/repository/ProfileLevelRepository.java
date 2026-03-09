package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.ProfileLevel;
import com.jp.be_jplearning.entity.ProfileLevelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileLevelRepository extends JpaRepository<ProfileLevel, ProfileLevelId> {
    List<ProfileLevel> findByIdProfileId(Long profileId);

    @Query("SELECT pl FROM ProfileLevel pl JOIN FETCH pl.level WHERE pl.id.profileId = :profileId")
    List<ProfileLevel> findByProfileIdWithLevel(@Param("profileId") Long profileId);
}
