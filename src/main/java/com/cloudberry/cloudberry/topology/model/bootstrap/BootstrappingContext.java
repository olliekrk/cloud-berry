package com.cloudberry.cloudberry.topology.model.bootstrap;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.exception.bootstrap.MissingStreamException;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.TopologyEdge;
import lombok.Data;
import org.apache.kafka.streams.kstream.KStream;
import org.bson.types.ObjectId;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Data
public class BootstrappingContext {
    private Topology topology;
    private DirectedAcyclicGraph<ObjectId, TopologyEdge> topologyGraph;

    /**
     * Node output (id, edge name) -> Node output KStream
     */
    private Map<OutgoingEdge, KStream<String, ComputationEvent>> streams;

    public BootstrappingContext(Topology topology) {
        this.topology = topology;
        this.topologyGraph = topology.constructGraph();
        this.streams = new HashMap<>();
    }

    public void putStream(ObjectId nodeId, String edgeName, KStream<String, ComputationEvent> stream) {
        streams.put(new OutgoingEdge(nodeId, edgeName), stream);
    }

    public void putStream(ObjectId nodeId, KStream<String, ComputationEvent> stream) {
        streams.put(new OutgoingEdge(nodeId, TopologyEdge.DEFAULT_NAME), stream);
    }

    public Optional<KStream<String, ComputationEvent>> getStream(ObjectId nodeId, String edgeName) {
        return Optional.ofNullable(streams.get(new OutgoingEdge(nodeId, edgeName)));
    }

    public KStream<String, ComputationEvent> getStreamOrThrow(ObjectId nodeId, String edgeName) {
        return getStream(nodeId, edgeName).orElseThrow(() -> new MissingStreamException(nodeId));
    }

    public void removeStream(ObjectId nodeId, String edgeName) {
        streams.remove(new OutgoingEdge(nodeId, edgeName));
    }

    public Set<TopologyEdge> getIncomingEdges(ObjectId nodeId) {
        var graph = topologyGraph;
        return graph.incomingEdgesOf(nodeId);
    }

}
