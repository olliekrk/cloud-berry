package com.cloudberry.cloudberry;

import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.db.influx.model.DataFilters;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataWriter;
import com.cloudberry.cloudberry.db.influx.service.InfluxOrganizationService;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.event.logs.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.logs.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.logs.WorkplaceEvent;
import com.cloudberry.cloudberry.kafka.event.metadata.MetadataEvent;
import com.cloudberry.cloudberry.model.solution.Solution;
import com.cloudberry.cloudberry.model.solution.SolutionDetails;
import com.cloudberry.cloudberry.service.api.RawDataService;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnStartupRunner implements ApplicationRunner {

    private final InfluxOrganizationService influxOrganizationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logDefaultConfiguration();

        // good place for quick dev testing
    }

    private void sendSampleProblem(ObjectId computationId) {
        var problemEvent = new MetadataEvent(computationId, "No problem at all", Map.of(), Map.of());
        IntStream.range(0, 1).forEach(__ -> kafkaTemplate.send(KafkaTopics.Metadata.PROBLEM_DEFINITION_TOPIC, problemEvent));
    }

    private void sendSampleLogs(ObjectId computationId) {
        var workplaceEvent = new WorkplaceEvent(computationId, 1L, Map.of());
        var summaryEvent = new SummaryEvent(computationId, 21.37, 1L);
        var bestSolutionEvent = new BestSolutionEvent(
                computationId,
                new Solution(Map.of()),
                new SolutionDetails(1L, 2137L, 1L)
        );
        var computationEvent = new ComputationEvent(
                Instant.now(),
                "wild_logs",
                Map.of("level", 10, "wildness", 9),
                Map.of("computationId", computationId.toString())
        );

        kafkaTemplate.send(KafkaTopics.Logs.WORKPLACE_TOPIC, workplaceEvent);
        kafkaTemplate.send(KafkaTopics.Logs.SUMMARY_TOPIC, summaryEvent);
        kafkaTemplate.send(KafkaTopics.Logs.BEST_SOLUTION_TOPIC, bestSolutionEvent);
        kafkaTemplate.send(KafkaTopics.Generic.COMPUTATION_TOPIC, computationEvent);
    }

    private void logDefaultConfiguration() {
        log.info("Default Influx organization ID: {}", influxOrganizationService.getDefaultOrganizationId());
    }
}
