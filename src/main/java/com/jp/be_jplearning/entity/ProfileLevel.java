package com.jp.be_jplearning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.jp.be_jplearning.entity.enums.ProgressStatusEnum;

@Entity
@Table(name = "profile_level")
@Getter
@Setter
@NoArgsConstructor
public class ProfileLevel {

    @EmbeddedId
    private ProfileLevelId id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "progress_status_enum")
    private ProgressStatusEnum status = ProgressStatusEnum.LEARNING;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("profileId")
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("levelId")
    @JoinColumn(name = "level_id")
    private Level level;
}
