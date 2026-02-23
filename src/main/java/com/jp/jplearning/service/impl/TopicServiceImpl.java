package com.jp.jplearning.service.impl;

import com.jp.jplearning.entity.Level;
import com.jp.jplearning.entity.Topic;
import com.jp.jplearning.repository.LevelRepository;
import com.jp.jplearning.repository.TopicRepository;
import com.jp.jplearning.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final LevelRepository levelRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository, LevelRepository levelRepository) {
        this.topicRepository = topicRepository;
        this.levelRepository = levelRepository;
    }

    @Override
    public Topic createTopic(Integer levelId, Topic topic) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found with id: " + levelId));
        
        topic.setLevel(level);
        return topicRepository.save(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Topic> getTopicsByLevel(Integer levelId) {
        if (!levelRepository.existsById(levelId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found with id: " + levelId);
        }
        return topicRepository.findByLevel_LevelId(levelId);
    }

    @Override
    @Transactional(readOnly = true)
    public Topic getTopicById(Integer topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found with id: " + topicId));
    }

    @Override
    public Topic updateTopic(Integer topicId, Topic topicDetails) {
        Topic topic = getTopicById(topicId);
        
        topic.setTopicName(topicDetails.getTopicName());
        // If level needs to be updated, it would be passed. But usually we just update the name here.
        // If details contain a level object, we could check. But typical use case is renaming.
        
        return topicRepository.save(topic);
    }

    @Override
    public void deleteTopic(Integer topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found with id: " + topicId);
        }
        topicRepository.deleteById(topicId);
    }
}
