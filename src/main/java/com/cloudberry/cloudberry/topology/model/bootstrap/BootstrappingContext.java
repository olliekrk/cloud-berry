package com.cloudberry.cloudberry.topology.model.bootstrap;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.exception.bootstrap.MissingStreamException;
import com.cloudberry.cloudberry.topology.model.Topology;
import lombok.Data;
import org.apache.kafka.streams.kstream.KStream;
import org.bson.types.ObjectId;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class BootstrappingContext {
    private Topology topology;
    private DirectedAcyclicGraph<ObjectId, DefaultEdge> topologyGraph;

    /**
     * Node ID -> Node output KStream
     */
    private Map<ObjectId, KStream<String, ComputationEvent>> streams;

    public BootstrappingContext(Topology topology) {
        this.topology = topology;
        this.topologyGraph = topology.constructGraph();
        this.streams = new HashMap<>();
    }

    public void putStream(ObjectId nodeId, KStream<String, ComputationEvent> stream) {
        streams.put(nodeId, stream);
    }

    public Optional<KStream<String, ComputationEvent>> getStream(ObjectId nodeId) {
        return Optional.ofNullable(streams.get(nodeId));
    }

    public KStream<String, ComputationEvent> getStreamOrThrow(ObjectId nodeId) {
        return getStream(nodeId).orElseThrow(() -> new MissingStreamException(nodeId));
    }

    public void removeStream(ObjectId nodeId) {
        streams.remove(nodeId);
    }

    public Set<ObjectId> getPredecessorNodesIds(ObjectId nodeId) {
        var graph = topologyGraph;
        return graph
                .incomingEdgesOf(nodeId)
                .stream()
                .map(graph::getEdgeSource)
                .collect(Collectors.toSet());
    }

}
