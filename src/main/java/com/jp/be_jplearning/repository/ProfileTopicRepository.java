package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.ProfileTopic;
import com.jp.be_jplearning.entity.ProfileTopicId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileTopicRepository extends JpaRepository<ProfileTopic, ProfileTopicId> {
    List<ProfileTopic> findByIdProfileIdAndTopicLevelId(Long profileId, Long levelId);
}
