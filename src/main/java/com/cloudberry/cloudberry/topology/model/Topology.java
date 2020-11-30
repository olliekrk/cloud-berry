package com.cloudberry.cloudberry.topology.model;


import com.cloudberry.cloudberry.common.syntax.SetSyntax;
import com.cloudberry.cloudberry.topology.exception.NodeNotInTopologyException;
import com.cloudberry.cloudberry.topology.exception.NotADirectedAcyclicGraphException;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Data
@Document
@AllArgsConstructor
public class Topology {

    @Id
    private ObjectId id;

    /**
     * Name of this topology.
     */
    private String name;

    /**
     * Whether this topology is default one generated on each system startup, or one defined or modified by user.
     */
    private boolean userDefined;

    /**
     * Directed edges representing the topology.
     * keys == vertices
     */
    private Map<ObjectId, Set<TopologyEdge>> edges;


    /**
     * @return set with ids of root vertices
     */
    public Set<ObjectId> findRootIds() {
        var graph = constructGraph();
        return graph.vertexSet().stream()
                .filter(vertex -> graph.inDegreeOf(vertex) == 0) // root vertices have 0 incoming edges
                .collect(Collectors.toSet());
    }

    /**
     * Valid graph is one that has at least one root vertex.
     */
    public boolean isValid() {
        return findRootIds().size() > 0;
    }

    public void addEdge(TopologyNode source, TopologyNode target) {
        addEdge(source, target, false);
    }

    public void addEdge(
            TopologyNode source,
            TopologyNode target,
            boolean addVertexToTopologyIfNotAdded
    ) {
        Stream.of(source, target).forEach(node -> validateNodeExistsInTopology(node, addVertexToTopologyIfNotAdded));
        edges.merge(source.getId(), Set.of(TopologyEdge.defaultEdge(source.getId(), target.getId())), SetSyntax::merge);
    }

    public void addEdge(
            TopologyNode source,
            TopologyNode target,
            boolean addVertexToTopologyIfNotAdded,
            String edgeName
    ) {
        Stream.of(source, target).forEach(node -> validateNodeExistsInTopology(node, addVertexToTopologyIfNotAdded));
        edges.merge(
                source.getId(), Set.of(new TopologyEdge(edgeName, source.getId(), target.getId())), SetSyntax::merge);
    }


    public void removeEdge(ObjectId source, ObjectId target) {
        edges.merge(
                source,
                Set.of(),
                (a, b) -> SetSyntax.without(SetSyntax.merge(a, b), TopologyEdge.defaultEdge(source, target))
        );
    }

    public void removeNode(ObjectId nodeId) {
        edges.remove(nodeId);
        edges = edges.entrySet()
                .stream()
                .map(entry -> {
                    var source = entry.getKey();
                    var edgesUpdated = entry.getValue().stream()
                            .filter(edge -> !edge.getTarget().equals(nodeId))
                            .collect(Collectors.toSet());
                    return Map.entry(source, edgesUpdated);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean containsVertex(TopologyNode node) {
        return edges.containsKey(node.getId());
    }

    public void addVertex(TopologyNode node) {
        edges.putIfAbsent(node.getId(), Set.of());
    }

    public Set<ObjectId> getVertices() {
        return edges.keySet();
    }

    public DirectedAcyclicGraph<ObjectId, TopologyEdge> constructGraph() {
        try {
            var graphBuilder = DirectedAcyclicGraph.<ObjectId, TopologyEdge>createBuilder(TopologyEdge.class);
            edges.keySet().forEach(graphBuilder::addVertex);
            edges.forEach((source, edges) -> edges
                    .forEach(edge -> graphBuilder.addEdge(edge.getSource(), edge.getTarget(), edge)));
            return graphBuilder.build();
        } catch (IllegalArgumentException e) { // thrown when DAG cannot be built
            log.warn("Topology is not an directed acyclic graph!", e);
            throw new NotADirectedAcyclicGraphException("Topology must represent directed acyclic graph!");
        }
    }

    private void validateNodeExistsInTopology(TopologyNode node, boolean addVertexToTopologyIfNotAdded) {
        if (!containsVertex(node)) {
            if (addVertexToTopologyIfNotAdded) {
                log.info("Adding vertex {} to topology {} automatically.", node, this);
                addVertex(node);
            } else {
                throw new NodeNotInTopologyException(node.getId(), getId());
            }
        }
    }
}
