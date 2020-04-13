package com.cloudberry.cloudberry.processing.extractors;

import com.cloudberry.cloudberry.converters.EventToLogConverter;
import com.cloudberry.cloudberry.model.event.Event;
import com.cloudberry.cloudberry.repository.facades.LogsRepositoryFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogsExtractor {

    private final LogsRepositoryFacade repositoryFacade;
    private final EventToLogConverter converter;

    @Async
    public void extractAndSave(Event event) {
        converter.toLog(event)
                .map(repositoryFacade::save)
                .subscribe();
    }

}
