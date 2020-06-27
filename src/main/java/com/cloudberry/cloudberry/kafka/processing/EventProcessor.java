package com.cloudberry.cloudberry.kafka.processing;


import com.cloudberry.cloudberry.kafka.event.Event;

public interface EventProcessor<T extends Event> {
    void process(T event);
}
