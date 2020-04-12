package com.cloudberry.cloudberry.processing.extractors;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.repository.facades.MetadataRepositoryFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MetadataExtractor {

    private final MetadataRepositoryFacade metadataRepositoryFacade;

    public MetadataExtractor(MetadataRepositoryFacade metadataRepositoryFacade) {
        this.metadataRepositoryFacade = metadataRepositoryFacade;
    }

    @Async
    public void extractAndSave(ProblemDefinitionEvent event) {
        metadataRepositoryFacade.saveMetadata(event).subscribe();
    }

}
