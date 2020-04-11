package com.cloudberry.cloudberry.service;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.repository.MetadataRepositoryFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MetadataProcessor {

    final MetadataRepositoryFacade metadataRepositoryFacade;

    public MetadataProcessor(MetadataRepositoryFacade metadataRepositoryFacade) {
        this.metadataRepositoryFacade = metadataRepositoryFacade;
    }

    @Async
    public void extractMetadataAndSave(ProblemDefinitionEvent event) {
        metadataRepositoryFacade.saveMetadata(event)
                .subscribe(__ -> log.info("Metadata save completed: " + Thread.currentThread().getName()));
    }

}
