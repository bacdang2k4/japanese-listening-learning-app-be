package com.jp.be_jplearning.repository;

import com.jp.be_jplearning.entity.ProfileTopic;
import com.jp.be_jplearning.entity.ProfileTopicId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileTopicRepository extends JpaRepository<ProfileTopic, ProfileTopicId> {
    List<ProfileTopic> findByIdProfileIdAndTopicLevelId(Long profileId, Long levelId);

    @Query("SELECT pt FROM ProfileTopic pt JOIN FETCH pt.topic WHERE pt.id.profileId = :profileId")
    List<ProfileTopic> findByProfileIdWithTopic(@Param("profileId") Long profileId);
}
