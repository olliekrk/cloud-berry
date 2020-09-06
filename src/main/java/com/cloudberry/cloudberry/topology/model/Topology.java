package com.cloudberry.cloudberry.topology.model;


import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
     * Directed edges representing the topology.
     */
    private Map<ObjectId, Set<ObjectId>> edges;

    /**
     * @return id of root vertex, assuming there is one and only one in a topology
     */
    public ObjectId findRootId() {
        var graph = constructGraph();
        // todo, to improve, consider multiple root nodes
        return graph.vertexSet().stream()
                .filter(vertex -> graph.inDegreeOf(vertex) == 0)
                .findAny()
                .orElse(null);
    }

    public Set<ObjectId> findAdjacentVertices(TopologyNode node) {
        return edges.getOrDefault(node.getId(), Set.of());
    }

    public boolean isValid() {
//        var graph = constructGraph();
        // todo
        return true;
    }

    public void addEdge(TopologyNode source, TopologyNode target) {
        edges.compute(
                source.getId(),
                (sourceId, sourceOutEdges) -> {
                    if (sourceOutEdges == null) {
                        return Set.of(target.getId());
                    } else {
                        var updatedOutEdges = new HashSet<>(sourceOutEdges);
                        updatedOutEdges.add(target.getId());
                        return updatedOutEdges;
                    }
                }
        );
    }

    public void addVertex(TopologyNode node) {
        edges.putIfAbsent(node.getId(), Set.of());
    }

    public DefaultDirectedGraph<ObjectId, DefaultEdge> constructGraph() {
        var graph = new DefaultDirectedGraph<ObjectId, DefaultEdge>(DefaultEdge.class);
        edges.keySet().forEach(graph::addVertex);
        edges.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(target -> Pair.of(entry.getKey(), target)))
                .forEach(edge -> graph.addEdge(edge.getFirst(), edge.getSecond()));
        return graph;
    }
}
