package com.cloudberry.cloudberry.rest;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.event.metadata.MetadataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@RequestMapping("kafka")
@Slf4j
@RequiredArgsConstructor
public class KafkaTestController {

    private static final String topicName = KafkaTopics.Metadata.PROBLEM_DEFINITION_TOPIC;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("ping")
    public String ping() throws InterruptedException {
        this.kafkaTemplate.send(topicName, new MetadataEvent(ObjectId.get(), "PING", Map.of(), Map.of()));
        return "All messages received";
    }

    // fixme: this method is only for debugging / testing
    @PostMapping("pingEvent")
    public void pingEvent() {
        var measurementName = "bootstrapping-streams-test-events";
        IntStream.range(0, 3)
                .boxed()
                .map(i -> {
                    log.info("Sending computation event no. {} to '{}' measurement", i, measurementName);
                    return new ComputationEvent(
                            Instant.now().plusSeconds(i),
                            measurementName,
                            Map.of("attempt", 1, "eventNumber", i),
                            Map.of()
                    );
                })
                .forEach(event -> kafkaTemplate.send(KafkaTopics.Generic.COMPUTATION_TOPIC, event));
    }
}
