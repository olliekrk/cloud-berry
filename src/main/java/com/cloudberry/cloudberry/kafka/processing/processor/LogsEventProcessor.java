package com.cloudberry.cloudberry.kafka.processing.processor;

import com.cloudberry.cloudberry.db.mongo.data.logs.MongoLog;
import com.cloudberry.cloudberry.db.mongo.repository.facades.LogsRepositoryFacade;
import com.cloudberry.cloudberry.kafka.event.Event;
import com.cloudberry.cloudberry.kafka.processing.EventProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LogsEventProcessor implements EventProcessor<Event> {

    private final LogsRepositoryFacade repositoryFacade;
    private final Converter<Event, MongoLog> converter;

    @Async
    public void process(Event event) {
        Mono.justOrEmpty(converter.convert(event))
                .map(repositoryFacade::save)
                .subscribe();
    }

}
