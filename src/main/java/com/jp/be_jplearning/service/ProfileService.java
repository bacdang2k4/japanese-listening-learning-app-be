package com.jp.be_jplearning.service;

import com.jp.be_jplearning.dto.CreateProfileRequest;
import com.jp.be_jplearning.dto.ProfileProgressResponse;
import com.jp.be_jplearning.dto.ProfileResponse;

import java.util.List;

public interface ProfileService {
    List<ProfileResponse> getMyProfiles();

    ProfileResponse createProfile(CreateProfileRequest request);

    ProfileProgressResponse getProfileProgress(Long profileId);
}
