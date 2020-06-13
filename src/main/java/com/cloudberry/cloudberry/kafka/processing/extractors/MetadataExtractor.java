package com.cloudberry.cloudberry.kafka.processing.extractors;

import com.cloudberry.cloudberry.util.converters.MetadataConverter;
import com.cloudberry.cloudberry.kafka.event.MetadataEvent;
import com.cloudberry.cloudberry.repository.facades.MetadataRepositoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MetadataExtractor {

    private final MetadataRepositoryFacade repositoryFacade;
    private final MetadataConverter converter;

    @Async
    public void extractAndSave(MetadataEvent event) {
        Mono.just(converter.toExperiment(event))
                .flatMap(repositoryFacade::save)
                .map(experiment -> converter.toConfiguration(event, experiment))
                .flatMap(repositoryFacade::save)
                .map(configuration -> converter.toEvaluation(event, configuration))
                .flatMap(repositoryFacade::save)
                .subscribe();
    }

}
