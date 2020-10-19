package com.cloudberry.cloudberry.topology.exception.bootstrap;

public class MissingStreamException extends BootstrappingException {
    public MissingStreamException(String topicName) {
        super("No stream for topic: " + topicName + " was found within the bootstrapping context");
    }
}
