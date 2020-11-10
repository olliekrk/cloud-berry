package com.cloudberry.cloudberry.topology.service;

import com.cloudberry.cloudberry.topology.model.Topology;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopologyModifyingService {
    private final TopologyService topologyService;
    private final TopologyNodeService topologyNodeService;

    public Topology deleteEdge(ObjectId topologyId, ObjectId sourceNodeId, ObjectId targetNodeId) {
        val topology = topologyService.findByIdOrThrow(topologyId);
        topology.removeEdge(sourceNodeId, targetNodeId);
        log.info("Deleted edge of topology: %s, source: %s, target: %s"
                         .formatted(topologyId, sourceNodeId, targetNodeId));
        topologyService.save(topology);
        return topology;
    }


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

        topology.addEdge(sourceNode, targetNode, addVertexToTopologyIfNotAdded);

        topologyService.save(topology);
        return topology;
    }

    public Topology addNodeBetweenNodes(
            ObjectId topologyId, ObjectId sourceNodeId, ObjectId insertedNodeId, ObjectId targetNodeId,
            boolean addVertexToTopologyIfNotAdded
    ) {
        addEdgeToTopology(topologyId, sourceNodeId, insertedNodeId, addVertexToTopologyIfNotAdded);
        addEdgeToTopology(topologyId, insertedNodeId, targetNodeId, addVertexToTopologyIfNotAdded);
        return deleteEdge(topologyId, sourceNodeId, targetNodeId);
    }

}
