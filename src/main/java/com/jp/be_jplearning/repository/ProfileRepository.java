package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
