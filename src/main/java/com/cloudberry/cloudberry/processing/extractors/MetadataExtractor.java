package com.cloudberry.cloudberry.processing.extractors;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.repository.facades.MetadataSaver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MetadataExtractor {

    private final MetadataSaver metadataSaver;

    public MetadataExtractor(MetadataSaver metadataSaver) {
        this.metadataSaver = metadataSaver;
    }

    @Async
    public void extractAndSave(ProblemDefinitionEvent event) {
        metadataSaver.saveMetadata(event).subscribe();
    }

}
