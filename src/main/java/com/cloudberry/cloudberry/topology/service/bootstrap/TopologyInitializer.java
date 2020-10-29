package com.cloudberry.cloudberry.topology.service.bootstrap;


import com.cloudberry.cloudberry.AppConstants;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.TopologySetupData;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import com.cloudberry.cloudberry.topology.service.TopologyNodeService;
import com.cloudberry.cloudberry.topology.service.TopologyService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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
        var topologySetupData = getDefaultTopologySetupData();
        return saveTopologySetupData(topologySetupData);
    }

    private Topology saveTopologySetupData(TopologySetupData topologySetupData) {
        var topology = saveTopology(topologySetupData.getTopology());
        saveNodes(topologySetupData.getNodes());
        return topology;
    }

    private TopologySetupData getDefaultTopologySetupData() {
        var topology = new Topology(ObjectId.get(), DEFAULT_TOPOLOGY_NAME, false, new HashMap<>());
        var initTopicName = KafkaTopics.Generic.COMPUTATION_TOPIC;
        var rootNode = new RootNode("root", initTopicName);
        var sinkNode = new SinkNode("sink", initTopicName, influxConfig.getDefaultStreamsBucketName());

        topology.addVertex(rootNode);
        topology.addVertex(sinkNode);
        topology.addEdge(rootNode, sinkNode);

        return new TopologySetupData(topology, List.of(rootNode, sinkNode));
    }

    private Topology saveTopology(Topology topology) {
        return topologyService.save(topology);
    }

    private void saveNodes(List<TopologyNode> nodes) {
        topologyNodeService.saveAll(nodes);
    }

    public void purgeDefaults() {
        var defaultTopologies = topologyService.findDefaultTopologies();
        topologyService.removeByIds(ListSyntax.mapped(defaultTopologies, Topology::getId));
        topologyNodeService.removeByIds(ListSyntax.flatMapped(defaultTopologies, Topology::getVertices));
    }
}
