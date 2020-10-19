package com.cloudberry.cloudberry.topology.service.bootstrap;


import com.cloudberry.cloudberry.AppConstants;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import com.cloudberry.cloudberry.topology.service.TopologyNodeService;
import com.cloudberry.cloudberry.topology.service.TopologyService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TopologyInitializer {
    private final TopologyService topologyService;
    private final TopologyNodeService topologyNodeService;
    private final InfluxConfig influxConfig;

    private final static String DEFAULT_TOPOLOGY_NAME = AppConstants.APPLICATION_NAME + " (Default)";

    public Topology buildDefaultTopology() {
        var topologyAndNodes = getDefaultTopology();
        var topology = saveTopology(topologyAndNodes.getFirst());
        saveNodes(topologyAndNodes.getSecond());
        return topology;
    }

    private Pair<Topology, List<TopologyNode>> getDefaultTopology() {
        var topology = new Topology(ObjectId.get(), DEFAULT_TOPOLOGY_NAME, new HashMap<>());
        var initTopicName = KafkaTopics.Generic.COMPUTATION_TOPIC;
        var rootNode = new RootNode("root", initTopicName);
        var sinkNode = new SinkNode("sink", initTopicName, influxConfig.getDefaultBucketName());
        topology.addVertex(rootNode);
        topology.addVertex(sinkNode);
        topology.addEdge(rootNode, sinkNode);
        return Pair.of(topology, List.of(rootNode, sinkNode));
    }

    private Topology saveTopology(Topology topology) {
        return this.topologyService.save(topology);
    }

    private List<TopologyNode> saveNodes(List<TopologyNode> nodes) {
        return this.topologyNodeService.saveAll(nodes);
    }

    public void purgeDefaults() {
        var defaultTopologies = topologyService.findByName(DEFAULT_TOPOLOGY_NAME);
        topologyService.removeByIds(ListSyntax.mapped(defaultTopologies, Topology::getId));
        topologyNodeService.removeByIds(ListSyntax.flatMapped(defaultTopologies, Topology::getVertices));
    }
}
