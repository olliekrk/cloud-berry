package com.cloudberry.cloudberry.topology.model;


import com.cloudberry.cloudberry.common.syntax.SetSyntax;
import com.cloudberry.cloudberry.topology.exception.NotADirectedAcyclicGraphException;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private boolean isUserDefined;

    /**
     * Directed edges representing the topology.
     * keys == vertices
     */
    private Map<ObjectId, Set<ObjectId>> edges;


    /**
     * @return set with ids of root vertices
     */
    public Set<ObjectId> findRootIds() {
        var graph = constructGraph();
        return graph.vertexSet().stream()
                .filter(vertex -> graph.inDegreeOf(vertex) == 0) // root vertices have 0 incoming edges
                .collect(Collectors.toSet());
    }

    public Set<ObjectId> findAdjacentVertices(TopologyNode node) {
        return edges.getOrDefault(node.getId(), Set.of());
    }

    /**
     * Valid graph is one that has at least one root vertex.
     */
    public boolean isValid() {
        return findRootIds().size() > 0;
    }

    public void addEdge(TopologyNode source, TopologyNode target) {
        edges.merge(source.getId(), Set.of(target.getId()), SetSyntax::merge);
    }

    public void addVertex(TopologyNode node) {
        edges.putIfAbsent(node.getId(), Set.of());
    }

    public Set<ObjectId> getVertices() {
        return edges.keySet();
    }

    public DirectedAcyclicGraph<ObjectId, DefaultEdge> constructGraph() {
        try {
            var graphBuilder = DirectedAcyclicGraph.<ObjectId, DefaultEdge>createBuilder(DefaultEdge.class);
            edges.keySet().forEach(graphBuilder::addVertex);
            edges.forEach((source, targets) -> targets.forEach(target -> graphBuilder.addEdge(source, target)));
            return graphBuilder.build();
        } catch (IllegalArgumentException e) { // thrown when DAG cannot be built
            log.warn("Topology is not an directed acyclic graph!", e);
            throw new NotADirectedAcyclicGraphException("Topology must represent directed acyclic graph!");
        }
    }
}
