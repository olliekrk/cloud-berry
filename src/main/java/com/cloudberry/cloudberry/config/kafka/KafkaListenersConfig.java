package com.cloudberry.cloudberry.config.kafka;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.model.logs.BestSolutionLog;
import com.cloudberry.cloudberry.repository.BestSolutionLogsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class KafkaListenersConfig {

    private final BestSolutionLogsRepository bestSolutionLogsRepository;

    public KafkaListenersConfig(BestSolutionLogsRepository bestSolutionLogsRepository) {
        this.bestSolutionLogsRepository = bestSolutionLogsRepository;
    }

    @KafkaListener(topics = KafkaTopicsConfig.PROBLEM_DEFINITION_TOPIC, clientIdPrefix = "json", containerFactory = "kafkaListenerContainerFactory")
    public void problemDefinitionListener(ConsumerRecord<String, ProblemDefinitionEvent> cr,
                                          @Payload ProblemDefinitionEvent payload) {
        BestSolutionLog bestSolutionLog = new BestSolutionLog();
        bestSolutionLogsRepository.save(bestSolutionLog).block();
//        log.info("Logger 1 [JSON] received key {}: Type [toodo] | Payload: {} | Record: {}", cr.key(), payload, cr.toString());
    }

}
