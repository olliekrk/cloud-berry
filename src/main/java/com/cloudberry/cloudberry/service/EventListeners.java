package com.cloudberry.cloudberry.service;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventListeners {

    private final MetadataProcessor metadataProcessor;

    public EventListeners(MetadataProcessor metadataProcessor) {
        this.metadataProcessor = metadataProcessor;
    }

    @KafkaListener(topics = KafkaTopics.PROBLEM_DEFINITION_TOPIC)
    public void problemDefinitionListener(ConsumerRecord<String, ProblemDefinitionEvent> consumer,
                                          @Payload ProblemDefinitionEvent event) {
        metadataProcessor.extractMetadataAndSave(event);
    }

}
