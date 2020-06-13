package com.cloudberry.cloudberry.kafka.processing;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.kafka.event.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.MetadataEvent;
import com.cloudberry.cloudberry.kafka.event.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.WorkplaceEvent;
import com.cloudberry.cloudberry.kafka.processing.extractors.LogsExtractor;
import com.cloudberry.cloudberry.kafka.processing.extractors.MetadataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventListeners {

    private final MetadataExtractor metadataExtractor;
    private final LogsExtractor logsExtractor;

    public EventListeners(MetadataExtractor metadataExtractor, LogsExtractor logsExtractor) {
        this.metadataExtractor = metadataExtractor;
        this.logsExtractor = logsExtractor;
    }

    @KafkaListener(topics = KafkaTopics.Metadata.PROBLEM_DEFINITION_TOPIC)
    public void problemDefinitionListener(ConsumerRecord<String, MetadataEvent> consumer,
                                          @Payload MetadataEvent event) {
        metadataExtractor.extractAndSave(event);
    }

    @KafkaListener(topics = KafkaTopics.Logs.WORKPLACE_TOPIC)
    public void workplaceListener(ConsumerRecord<String, WorkplaceEvent> consumer,
                                  @Payload WorkplaceEvent event) {
        logsExtractor.extractAndSave(event);
    }

    @KafkaListener(topics = KafkaTopics.Logs.SUMMARY_TOPIC)
    public void summaryListener(ConsumerRecord<String, SummaryEvent> consumer,
                                @Payload SummaryEvent event) {
        logsExtractor.extractAndSave(event);
    }

    @KafkaListener(topics = KafkaTopics.Logs.BEST_SOLUTION_TOPIC)
    public void bestSolutionListener(ConsumerRecord<String, BestSolutionEvent> consumer,
                                     @Payload BestSolutionEvent event) {
        logsExtractor.extractAndSave(event);
    }
}
