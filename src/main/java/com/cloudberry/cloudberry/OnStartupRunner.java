package com.cloudberry.cloudberry;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.db.influx.data.WorkplaceLogMeasurement;
import com.cloudberry.cloudberry.db.influx.service.InfluxMeasurementWriter;
import com.cloudberry.cloudberry.kafka.event.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.MetadataEvent;
import com.cloudberry.cloudberry.kafka.event.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.WorkplaceEvent;
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
    private final InfluxMeasurementWriter influxDBConnector;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // good place for quick dev testing
        var evaluationId = UUID.randomUUID();

        sendSampleProblem(evaluationId);
        sendSampleLogs(evaluationId);
        writeSampleMeasurements(evaluationId);
    }

    private void writeSampleMeasurements(UUID evaluationId) {
        var measurement = new WorkplaceLogMeasurement(0L, evaluationId.toString(), Instant.now());
        influxDBConnector.writeMeasurement(measurement);
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

        kafkaTemplate.send(KafkaTopics.Logs.WORKPLACE_TOPIC, workplaceEvent);
        kafkaTemplate.send(KafkaTopics.Logs.SUMMARY_TOPIC, summaryEvent);
        kafkaTemplate.send(KafkaTopics.Logs.BEST_SOLUTION_TOPIC, bestSolutionEvent);
    }
}
