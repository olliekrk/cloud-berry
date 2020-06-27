package com.cloudberry.cloudberry.kafka.processing.processor;

import com.cloudberry.cloudberry.db.mongo.util.processor.MongoMetadataProcessor;
import com.cloudberry.cloudberry.kafka.event.metadata.MetadataEvent;
import com.cloudberry.cloudberry.db.mongo.repository.facades.MetadataRepositoryFacade;
import com.cloudberry.cloudberry.kafka.processing.EventProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MetadataEventProcessor implements EventProcessor<MetadataEvent> {

    private final MetadataRepositoryFacade repositoryFacade;
    private final MongoMetadataProcessor converter;

    @Override
    @Async
    public void process(MetadataEvent event) {
        Mono.just(converter.extractExperimentData(event))
                .flatMap(repositoryFacade::save)
                .map(experiment -> converter.extractConfigurationData(event, experiment))
                .flatMap(repositoryFacade::save)
                .map(configuration -> converter.extractEvaluationData(event, configuration))
                .flatMap(repositoryFacade::save)
                .subscribe();
    }

}
