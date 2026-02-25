package com.jp.be_jplearning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ProfileTopicId implements Serializable {

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "topic_id")
    private Long topicId;
}
