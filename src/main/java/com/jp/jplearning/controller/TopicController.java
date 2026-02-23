package com.jp.jplearning.controller;

import com.jp.jplearning.entity.Topic;
import com.jp.jplearning.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Topic Management", description = "APIs for managing topics within levels")
public class TopicController {

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping("/levels/{levelId}/topic")
    @Operation(summary = "Create a new topic", description = "Create a new Topic for a specific Level")
    public ResponseEntity<Topic> createTopic(@PathVariable Integer levelId, @RequestBody Topic topic) {
        Topic createdTopic = topicService.createTopic(levelId, topic);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    @GetMapping("/levels/{levelId}/topic")
    @Operation(summary = "Get all topics of a level", description = "Retrieve all Topics belonging to a specific Level")
    public ResponseEntity<List<Topic>> getTopicsByLevel(@PathVariable Integer levelId) {
        List<Topic> topics = topicService.getTopicsByLevel(levelId);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/topics/{topicId}")
    @Operation(summary = "Get topic by ID", description = "Retrieve a specific Topic by its unique ID")
    public ResponseEntity<Topic> getTopicById(@PathVariable Integer topicId) {
        Topic topic = topicService.getTopicById(topicId);
        return ResponseEntity.ok(topic);
    }

    @PutMapping("/topics/{topicId}")
    @Operation(summary = "Update a topic", description = "Update details of an existing Topic")
    public ResponseEntity<Topic> updateTopic(@PathVariable Integer topicId, @RequestBody Topic topic) {
        Topic updatedTopic = topicService.updateTopic(topicId, topic);
        return ResponseEntity.ok(updatedTopic);
    }

    @DeleteMapping("/topics/{topicId}")
    @Operation(summary = "Delete a topic", description = "Delete a Topic by its unique ID")
    public ResponseEntity<Void> deleteTopic(@PathVariable Integer topicId) {
        topicService.deleteTopic(topicId);
        return ResponseEntity.noContent().build();
    }
}
