package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.ProfileLevel;
import com.jp.be_jplearning.entity.ProfileLevelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileLevelRepository extends JpaRepository<ProfileLevel, ProfileLevelId> {
}
