package com.cloudberry.cloudberry.kafka.processing;

import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.event.logs.BestSolutionEvent;
import com.cloudberry.cloudberry.kafka.event.logs.SummaryEvent;
import com.cloudberry.cloudberry.kafka.event.logs.WorkplaceEvent;
import com.cloudberry.cloudberry.kafka.event.metadata.MetadataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListeners {

    private final EventProcessor<ComputationEvent> computationEventProcessor;
    private final EventProcessor<MetadataEvent> metadataEventProcessor;
    private final EventProcessor<Event> logsEventProcessor;

    @KafkaListener(topics = KafkaTopics.Metadata.PROBLEM_DEFINITION_TOPIC)
    public void problemDefinitionListener(
            ConsumerRecord<String, MetadataEvent> consumer,
            @Payload MetadataEvent event
    ) {
        metadataEventProcessor.process(event);
    }

    @KafkaListener(topics = KafkaTopics.Logs.WORKPLACE_TOPIC)
    public void workplaceListener(
            ConsumerRecord<String, WorkplaceEvent> consumer,
            @Payload WorkplaceEvent event
    ) {
        logsEventProcessor.process(event);
    }

    @KafkaListener(topics = KafkaTopics.Logs.SUMMARY_TOPIC)
    public void summaryListener(
            ConsumerRecord<String, SummaryEvent> consumer,
            @Payload SummaryEvent event
    ) {
        logsEventProcessor.process(event);
    }

    @KafkaListener(topics = KafkaTopics.Logs.BEST_SOLUTION_TOPIC)
    public void bestSolutionListener(
            ConsumerRecord<String, BestSolutionEvent> consumer,
            @Payload BestSolutionEvent event
    ) {
        logsEventProcessor.process(event);
    }

    @KafkaListener(topics = KafkaTopics.Generic.COMPUTATION_TOPIC)
    public void computationListener(
            ConsumerRecord<String, ComputationEvent> consumer,
            @Payload ComputationEvent event
    ) {
        computationEventProcessor.process(event);
    }

}
