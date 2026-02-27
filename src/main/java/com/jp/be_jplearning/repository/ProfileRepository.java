package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByLearnerId(Long learnerId);
}
