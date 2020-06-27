package com.cloudberry.cloudberry.rest;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.kafka.event.metadata.MetadataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("kafka")
@Slf4j
public class KafkaTestController {

    private static final String topicName = KafkaTopics.Metadata.PROBLEM_DEFINITION_TOPIC;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaTestController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("ping")
    public String ping() throws InterruptedException {
        this.kafkaTemplate.send(topicName, new MetadataEvent(UUID.randomUUID(), "PING", Map.of(), Map.of()));
        return "All messages received";
    }
}
