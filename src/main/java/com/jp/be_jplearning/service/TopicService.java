package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.PaginationResponse;
import com.jp.be_jplearning.dto.TopicRequest;
import com.jp.be_jplearning.dto.TopicResponse;

public interface TopicService {
    PaginationResponse<TopicResponse> getTopics(int page, int size, Long levelId, String keyword, String sort);

    TopicResponse getTopicById(Long topicId);

    TopicResponse createTopic(TopicRequest request);

    TopicResponse updateTopic(Long topicId, TopicRequest request);

    void deleteTopic(Long topicId);
}
