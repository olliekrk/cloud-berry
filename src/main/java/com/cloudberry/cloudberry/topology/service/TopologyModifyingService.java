package com.cloudberry.cloudberry.topology.service;

import com.cloudberry.cloudberry.topology.exception.NodeNotInTopologyException;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopologyModifyingService {
    private final TopologyService topologyService;
    private final TopologyNodeService topologyNodeService;

    public Topology addVertexToTopology(ObjectId topologyId, ObjectId topologyNodeId) {
        val topology = topologyService.findByIdOrThrow(topologyId);

        val topologyNode = topologyNodeService.findByIdOrThrow(topologyNodeId);

        topology.addVertex(topologyNode);

        topologyService.save(topology);
        return topology;
    }


    public Topology addEdgeToTopology(
            ObjectId topologyId, ObjectId sourceNodeId, ObjectId targetNodeId, boolean addVertexToTopologyIfNotAdded
    ) {
        val topology = topologyService.findByIdOrThrow(topologyId);

        val sourceNode = topologyNodeService.findByIdOrThrow(sourceNodeId);
        val targetNode = topologyNodeService.findByIdOrThrow(targetNodeId);

        validateNodeExistsInTopology(topology, sourceNode, addVertexToTopologyIfNotAdded);
        validateNodeExistsInTopology(topology, targetNode, addVertexToTopologyIfNotAdded);

        topology.addEdge(sourceNode, targetNode);

        topologyService.save(topology);
        return topology;
    }

    private void validateNodeExistsInTopology(
            Topology topology, TopologyNode node, boolean addVertexToTopologyIfNotAdded
    ) {
        if (!topology.containsVertex(node)) {
            if (addVertexToTopologyIfNotAdded) {
                topology.addVertex(node);
            } else {
                throw new NodeNotInTopologyException(node.getId(), topology.getId());
            }
        }
    }

}
