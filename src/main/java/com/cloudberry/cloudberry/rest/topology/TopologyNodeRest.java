package com.cloudberry.cloudberry.rest.topology;

import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidTopologyIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidTopologyNodeIdException;
import com.cloudberry.cloudberry.rest.util.TopologyIdDispatcher;
import com.cloudberry.cloudberry.topology.model.filtering.FilterExpression;
import com.cloudberry.cloudberry.topology.model.mapping.MappingExpression;
import com.cloudberry.cloudberry.topology.model.nodes.BranchNode;
import com.cloudberry.cloudberry.topology.model.nodes.CounterNode;
import com.cloudberry.cloudberry.topology.model.nodes.FilterNode;
import com.cloudberry.cloudberry.topology.model.nodes.MapNode;
import com.cloudberry.cloudberry.topology.model.nodes.MergeNode;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import com.cloudberry.cloudberry.topology.service.TopologyNodeService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topologyNode")
public class TopologyNodeRest {
    private final TopologyNodeService topologyNodeService;

    @PostMapping("/root")
    TopologyNode createRootNode(@RequestParam String name, @RequestParam String inputTopicName) {
        return topologyNodeService.save(new RootNode(name, inputTopicName));
    }

    @PostMapping("/counter")
    TopologyNode createCounterNode(@RequestParam String name, @RequestParam String metricName) {
        return topologyNodeService.save(new CounterNode(name, metricName));
    }

    @PostMapping("/sink")
    TopologyNode createSinkNode(@RequestParam String name, @RequestParam String outputBucketName) {
        return topologyNodeService.saveSinkNode(new SinkNode(name, outputBucketName));
    }

    @PostMapping("/filter")
    TopologyNode createFilterNode(@RequestParam String name, @RequestBody FilterExpression filterExpression) {
        return topologyNodeService.save(new FilterNode(name, filterExpression));
    }

    @PostMapping("/map")
    TopologyNode createMappingNode(@RequestParam String name, @RequestBody MappingExpression mappingExpression) {
        return topologyNodeService.save(new MapNode(name, mappingExpression));
    }

    @PostMapping("/merge")
    TopologyNode createMergeNode(@RequestParam String name) {
        return topologyNodeService.save(new MergeNode(name));
    }

    @PostMapping("/branch")
    TopologyNode createBranchNode(@RequestParam String name, @RequestBody FilterExpression filterExpression) {
        return topologyNodeService.save(new BranchNode(name, filterExpression));
    }

    @GetMapping("/all")
    List<TopologyNode> findAll() {
        return topologyNodeService.findAll();
    }

    @GetMapping("/name/{name}")
    List<TopologyNode> findAllByName(@PathVariable String name) {
        return topologyNodeService.findAllByName(name);
    }

    @GetMapping("/topology/{topologyIdHex}")
    List<TopologyNode> findAllByTopologyId(@PathVariable String topologyIdHex) throws InvalidTopologyIdException {
        val topologyId = TopologyIdDispatcher.getTopologyId(topologyIdHex);
        return topologyNodeService.findNodesUsedInTopology(topologyId);
    }

    @GetMapping("/id/{id}")
    Optional<TopologyNode> findById(@PathVariable String id)
            throws InvalidTopologyNodeIdException {
        val topologyNodeId = TopologyIdDispatcher.getTopologyNodeId(id);
        return topologyNodeService.findById(topologyNodeId);
    }

    @DeleteMapping("/id/{id}")
    void deleteById(@PathVariable String id)
            throws InvalidTopologyNodeIdException {
        val topologyNodeId = TopologyIdDispatcher.getTopologyNodeId(id);
        topologyNodeService.deleteById(topologyNodeId);
    }

    @DeleteMapping
    List<TopologyNode> deleteByIds(@RequestParam List<String> topologyNodesIdsHex)
            throws InvalidTopologyNodeIdException {
        val topologyNodesIds = TopologyIdDispatcher.getTopologyNodesIds(topologyNodesIdsHex);
        return topologyNodeService.deleteAllByIds(topologyNodesIds);
    }
}
