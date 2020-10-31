package com.cloudberry.cloudberry.topology.service.bootstrap;

import com.cloudberry.cloudberry.AppConstants;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.TopologySetupData;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultTopologyProvider {
    private final InfluxConfig influxConfig;
    private final static String DEFAULT_TOPOLOGY_NAME = AppConstants.APPLICATION_NAME + " (Default)";

    public TopologySetupData get() {
        var topology = new Topology(ObjectId.get(), DEFAULT_TOPOLOGY_NAME, false, new HashMap<>());
        var rootNode = new RootNode("root", KafkaTopics.Generic.COMPUTATION_TOPIC);
        var sinkNode = new SinkNode("sink", influxConfig.getDefaultStreamsBucketName());

        topology.addVertex(rootNode);
        topology.addVertex(sinkNode);
        topology.addEdge(rootNode, sinkNode);

        return new TopologySetupData(topology, List.of(rootNode, sinkNode));
    }

}
