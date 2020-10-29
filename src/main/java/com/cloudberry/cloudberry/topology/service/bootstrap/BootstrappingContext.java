package com.cloudberry.cloudberry.topology.service.bootstrap;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.exception.bootstrap.MissingStreamException;
import com.cloudberry.cloudberry.topology.model.Topology;
import lombok.Data;
import org.apache.kafka.streams.kstream.KStream;
import org.bson.types.ObjectId;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class BootstrappingContext {
    private Topology topology;
    private AbstractBaseGraph<ObjectId, DefaultEdge> topologyGraph;

    /**
     * Kafka topic name -> Stream's current definition
     */
    private Map<String, KStream<String, ComputationEvent>> streams;

    public BootstrappingContext(Topology topology) {
        this.topology = topology;
        this.topologyGraph = topology.constructGraph();
        this.streams = new HashMap<>();
    }

    public void addStream(String inputTopic, KStream<String, ComputationEvent> stream) {
        streams.put(inputTopic, stream);
    }

    public Optional<KStream<String, ComputationEvent>> getStream(String inputTopic) {
        return Optional.ofNullable(streams.get(inputTopic));
    }

    public KStream<String, ComputationEvent> getStreamOrThrow(String inputTopic) {
        return getStream(inputTopic)
                .orElseThrow(() -> new MissingStreamException(inputTopic));
    }

    public void removeStream(String inputTopic) {
        streams.remove(inputTopic);
    }
}
