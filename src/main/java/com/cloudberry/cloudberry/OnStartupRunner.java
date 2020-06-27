package com.cloudberry.cloudberry;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.event.logs.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.logs.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.logs.WorkplaceEvent;
import com.cloudberry.cloudberry.kafka.event.metadata.MetadataEvent;
import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class OnStartupRunner implements ApplicationRunner {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final InfluxDataWriter influxDBConnector;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // good place for quick dev testing
        var evaluationId = UUID.randomUUID();

        sendSampleProblem(evaluationId);
        sendSampleLogs(evaluationId);
    }

    private void sendSampleProblem(UUID evaluationId) {
        var problemEvent = new MetadataEvent(evaluationId, "No problem at all", Map.of(), Map.of());
        IntStream.range(0, 1).forEach(__ -> kafkaTemplate.send(KafkaTopics.Metadata.PROBLEM_DEFINITION_TOPIC, problemEvent));
    }

    private void sendSampleLogs(UUID evaluationId) {
        var workplaceEvent = new WorkplaceEvent(evaluationId, 1L, Map.of());
        var summaryEvent = new SummaryEvent(evaluationId, 21.37, 1L);
        var bestSolutionEvent = new BestSolutionEvent(
                evaluationId,
                new Solution(Map.of()),
                new SolutionDetails(1L, 2137L, 1L)
        );
        var computationEvent = new ComputationEvent(
                Instant.now(),
                "wild_logs",
                Map.of("level", 10, "wildness", 9),
                Map.of("evaluationId", evaluationId.toString())
        );

        kafkaTemplate.send(KafkaTopics.Logs.WORKPLACE_TOPIC, workplaceEvent);
        kafkaTemplate.send(KafkaTopics.Logs.SUMMARY_TOPIC, summaryEvent);
        kafkaTemplate.send(KafkaTopics.Logs.BEST_SOLUTION_TOPIC, bestSolutionEvent);
        kafkaTemplate.send(KafkaTopics.Generic.COMPUTATION_TOPIC, computationEvent);
    }
}
