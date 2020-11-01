package com.cloudberry.cloudberry.rest.topology;

import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidTopologyIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidTopologyNodeIdException;
import com.cloudberry.cloudberry.rest.util.TopologyIdDispatcher;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.service.TopologyModifyingService;
import com.cloudberry.cloudberry.topology.service.TopologyService;
import com.cloudberry.cloudberry.topology.service.reconfiguration.TopologyReconfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topology")
public class TopologyRest {
    private final TopologyService topologyService;
    private final TopologyModifyingService topologyModifyingService;
    private final TopologyReconfigurationService topologyReconfigurationService;

    @PostMapping("/create")
    Topology createTopology(@RequestParam String name) {
        return topologyService.save(new Topology(ObjectId.get(), name, true, Map.of()));
    }

    @GetMapping("/all")
    List<Topology> findTopologies() {
        return topologyService.findAll();
    }

    @GetMapping("/name/{name}")
    List<Topology> findTopologiesByName(@PathVariable String name) {
        return topologyService.findByName(name);
    }

    @GetMapping("/id/{id}")
    Optional<Topology> findTopologyById(@PathVariable String id) throws InvalidTopologyIdException {
        return topologyService.findById(TopologyIdDispatcher.getTopologyId(id));
    }

    @PostMapping("/id/{id}/use")
    void useTopology(@PathVariable String id) throws InvalidTopologyIdException {
        var topologyId = TopologyIdDispatcher.getTopologyId(id);
        topologyReconfigurationService.useTopology(topologyId);
    }

    @PutMapping("/id/{id}/addNode")
    Topology addNodeToTopology(
            @PathVariable("id") String topologyIdHex,
            @RequestParam String topologyNodeIdHex
    ) throws InvalidTopologyNodeIdException, InvalidTopologyIdException {
        val topologyId = TopologyIdDispatcher.getTopologyId(topologyIdHex);
        val topologyNodeId = TopologyIdDispatcher.getTopologyNodeId(topologyNodeIdHex);

        return topologyModifyingService.addVertexToTopology(topologyId, topologyNodeId);
    }

    @PutMapping("/id/{id}/addEdge")
    Topology addEdgeToTopology(
            @PathVariable("id") String topologyIdHex,
            @RequestParam String sourceNodeIdHex,
            @RequestParam String targetNodeIdHex,
            @RequestParam(defaultValue = "false") boolean addVertexToTopologyIfNotAdded
    ) throws InvalidTopologyNodeIdException, InvalidTopologyIdException {
        val topologyId = TopologyIdDispatcher.getTopologyId(topologyIdHex);
        val sourceNodeId = TopologyIdDispatcher.getTopologyNodeId(sourceNodeIdHex);
        val targetNodeId = TopologyIdDispatcher.getTopologyNodeId(targetNodeIdHex);

        return topologyModifyingService
                .addEdgeToTopology(topologyId, sourceNodeId, targetNodeId, addVertexToTopologyIfNotAdded);
    }

    @DeleteMapping("/id/{id}")
    void deleteTopology(@PathVariable String id) throws InvalidTopologyIdException {
        val topologyId = TopologyIdDispatcher.getTopologyId(id);
        topologyService.deleteById(topologyId);
    }

    @DeleteMapping
    List<Topology> deleteTopologies(@RequestParam List<String> topologiesIdsHex) throws InvalidTopologyIdException {
        val topologiesIds = TopologyIdDispatcher.getTopologiesIds(topologiesIdsHex);
        return topologyService.deleteAllByIds(topologiesIds);
    }
}
