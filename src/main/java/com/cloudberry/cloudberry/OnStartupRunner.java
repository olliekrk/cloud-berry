package com.cloudberry.cloudberry;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class OnStartupRunner implements ApplicationRunner {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public OnStartupRunner(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // good place for quick dev testing
        sendSampleEvents();
    }

    private void sendSampleEvents() {
        var event1 = new ProblemDefinitionEvent(UUID.randomUUID(), "Tranquillo", Map.of(), Map.of());
        IntStream.range(0, 1).forEach(__ -> kafkaTemplate.send(KafkaTopics.PROBLEM_DEFINITION_TOPIC, event1));
    }
}
