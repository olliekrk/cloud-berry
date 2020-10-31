package com.cloudberry.cloudberry.rest.topology;

import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidTopologyNodeIdException;
import com.cloudberry.cloudberry.rest.util.TopologyIdDispatcher;
import com.cloudberry.cloudberry.topology.model.filtering.FilterExpression;
import com.cloudberry.cloudberry.topology.model.nodes.CounterNode;
import com.cloudberry.cloudberry.topology.model.nodes.FilterNode;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import com.cloudberry.cloudberry.topology.service.TopologyNodeService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topology/node")
public class NodesRest {
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
        return topologyNodeService.save(new SinkNode(name, outputBucketName));
    }

    @PostMapping("/filter")
    TopologyNode createFilterNode(@RequestParam String name, @RequestBody FilterExpression filterExpression) {
        return topologyNodeService.save(new FilterNode(name, filterExpression));
    }

    @GetMapping("/all")
    List<TopologyNode> findAll() {
        return topologyNodeService.findAll();
    }

    @GetMapping("/byId")
    Optional<TopologyNode> findById(@RequestParam String topologyNodeIdHex) throws InvalidTopologyNodeIdException {
        val topologyNodeId = TopologyIdDispatcher.getTopologyNodeId(topologyNodeIdHex);
        return topologyNodeService.findById(topologyNodeId);
    }

    @GetMapping("/byName")
    List<TopologyNode> findAllByName(@RequestParam String name) {
        return topologyNodeService.findAllByName(name);
    }

    @DeleteMapping
    List<TopologyNode> deleteById(@RequestParam List<String> topologyNodesIdsHex)
            throws InvalidTopologyNodeIdException {
        val topologyNodesIds = TopologyIdDispatcher.getTopologyNodesIds(topologyNodesIdsHex);
        return topologyNodeService.removeByIds(topologyNodesIds);
    }
}
