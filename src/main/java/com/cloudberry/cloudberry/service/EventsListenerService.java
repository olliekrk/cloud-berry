package com.cloudberry.cloudberry.service;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import com.cloudberry.cloudberry.repository.BestSolutionLogsRepository;
import com.cloudberry.cloudberry.repository.ExperimentsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventsListenerService {

    private final ExperimentsRepository experimentsRepository;

    public EventsListenerService(ExperimentsRepository experimentsRepository) {
        this.experimentsRepository = experimentsRepository;
    }

    @KafkaListener(topics = KafkaTopics.PROBLEM_DEFINITION_TOPIC)
    public void problemDefinitionListener(ConsumerRecord<String, ProblemDefinitionEvent> cr,
                                          @Payload ProblemDefinitionEvent payload) {
        log.info("RECEIVED: " + payload.toString());
    }

}
