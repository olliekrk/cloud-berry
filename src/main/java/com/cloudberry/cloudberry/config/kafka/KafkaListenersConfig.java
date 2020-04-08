package com.cloudberry.cloudberry.config.kafka;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.model.logs.FitnessLog;
import com.cloudberry.cloudberry.repository.LogsRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.core.publisher.Mono;

@Configuration
public class KafkaListenersConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListenersConfig.class);

    private final LogsRepository logsRepository;

    public KafkaListenersConfig(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    @KafkaListener(topics = KafkaTopicsConfig.PROBLEM_DEFINITION_TOPIC, clientIdPrefix = "json", containerFactory = "kafkaListenerContainerFactory")
    public void problemDefinitionListener(ConsumerRecord<String, ProblemDefinitionEvent> cr,
                                          @Payload ProblemDefinitionEvent payload) {
        double fitness = payload.description.length();
        Mono<FitnessLog> mono = logsRepository.save(new FitnessLog(payload.eventTime, fitness));
        mono.block();
        logger.info("Logger 1 [JSON] received key {}: Type [todo] | Payload: {} | Record: {}", cr.key(), payload, cr.toString());
    }

}
