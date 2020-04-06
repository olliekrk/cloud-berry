package com.cloudberry.cloudberry.api;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.logging.Logging;
import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kafka")
public class KafkaTestController {

    @Logging
    private Logger logger;
    private final static String topicName = KafkaTopics.PROBLEM_DEFINITION_TOPIC;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaTestController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("ping")
    public String ping() throws InterruptedException {
        this.kafkaTemplate.send(topicName, new ProblemDefinitionEvent("This is problem"));
        logger.info("All messages received");
        return "All messages received";
    }
}
