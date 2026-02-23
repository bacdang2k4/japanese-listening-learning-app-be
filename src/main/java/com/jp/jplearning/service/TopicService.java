package com.jp.jplearning.service;

import com.jp.jplearning.entity.Topic;

import java.util.List;

public interface TopicService {
    Topic createTopic(Integer levelId, Topic topic);

    List<Topic> getTopicsByLevel(Integer levelId);

    Topic getTopicById(Integer topicId);

    Topic updateTopic(Integer topicId, Topic topicDetails);

    void deleteTopic(Integer topicId);
}
