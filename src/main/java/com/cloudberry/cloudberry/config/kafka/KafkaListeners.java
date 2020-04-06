package com.cloudberry.cloudberry.config.kafka;

import com.cloudberry.cloudberry.logging.Logging;
import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

@Configuration
public class KafkaListeners {

    @Logging
    private Logger logger;

    @KafkaListener(topics = KafkaTopics.PROBLEM_DEFINITION_TOPIC, clientIdPrefix = "json", containerFactory = "kafkaListenerContainerFactory")
    public void problemDefinitionListener(ConsumerRecord<String, ProblemDefinitionEvent> cr,
                                          @Payload ProblemDefinitionEvent payload) {
        logger.info("Logger 1 [JSON] received key {}: Type [todo] | Payload: {} | Record: {}", cr.key(), payload, cr.toString());
    }

}
