package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.AudioTestRequest;
import com.jp.be_jplearning.dto.AudioTestResponse;
import com.jp.be_jplearning.entity.enums.TestStatusEnum;

public interface AudioTestService {
    PaginationResponse<AudioTestResponse> getAudioTests(int page, int size, Long topicId, TestStatusEnum status,
            String keyword, String sortStr);

    AudioTestResponse getAudioTestById(Long testId);

    AudioTestResponse createAudioTest(AudioTestRequest request);

    AudioTestResponse updateAudioTest(Long testId, AudioTestRequest request);

    void deleteAudioTest(Long testId);

    void publishAudioTest(Long testId);

    void rejectAudioTest(Long testId);
}
