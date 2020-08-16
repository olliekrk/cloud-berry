package com.cloudberry.cloudberry.kafka.processing.processor;

import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.kafka.event.metadata.MetadataEvent;
import com.cloudberry.cloudberry.kafka.processing.EventProcessor;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MetadataEventProcessor implements EventProcessor<MetadataEvent> {

    private final MetadataService metadataService;

    @Override
    @Async
    public void process(MetadataEvent event) {
        Mono.just(extractExperimentData(event))
                .flatMap(metadataService::getOrCreateExperiment)
                .map(experiment -> extractConfigurationData(event, experiment.getId()))
                .flatMap(metadataService::getOrCreateConfiguration)
                .map(configuration -> extractEvaluationData(event, configuration.getId()))
                .flatMap(metadataService::getOrCreateEvaluation)
                .subscribe();
    }

    private static Experiment extractExperimentData(MetadataEvent event) {
        return new Experiment(
                event.getTime(),
                event.getExperimentName(),
                event.getExperimentParameters()
        );
    }

    private static ExperimentConfiguration extractConfigurationData(MetadataEvent event, ObjectId experimentId) {
        return new ExperimentConfiguration(
                new ObjectId(),
                experimentId,
                null,
                event.getConfigurationParameters(),
                event.getTime()
        );
    }

    private static ExperimentEvaluation extractEvaluationData(MetadataEvent event, ObjectId configurationId) {
        return new ExperimentEvaluation(
                event.getEvaluationId(),
                configurationId,
                event.getTime()
        );
    }

}
