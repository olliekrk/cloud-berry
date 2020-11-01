package com.cloudberry.cloudberry.topology.service.bootstrap;

import com.cloudberry.cloudberry.AppConstants;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.config.kafka.KafkaTopics;
import com.cloudberry.cloudberry.metrics.MetricsIndex;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.TopologySetupData;
import com.cloudberry.cloudberry.topology.model.filtering.FilterExpression;
import com.cloudberry.cloudberry.topology.model.filtering.FilterPredicate;
import com.cloudberry.cloudberry.topology.model.filtering.FilterType;
import com.cloudberry.cloudberry.topology.model.nodes.CounterNode;
import com.cloudberry.cloudberry.topology.model.nodes.FilterNode;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import com.cloudberry.cloudberry.topology.model.operators.EqualityOperator;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

import static com.cloudberry.cloudberry.topology.model.operators.LogicalOperator.AND;

@Component
@RequiredArgsConstructor
public class DefaultTopologyProvider {
    private final InfluxConfig influxConfig;
    private final static String DEFAULT_TOPOLOGY_NAME = AppConstants.APPLICATION_NAME + " (Default)";
    private static final FilterExpression FILTER_EXPRESSION = new FilterExpression(
            AND,
            List.of(),
            List.of(new FilterPredicate("eventNumber", "1", EqualityOperator.LT, FilterType.NUMERIC, true))
    );

    public TopologySetupData get() {
        var topology = new Topology(ObjectId.get(), DEFAULT_TOPOLOGY_NAME, false, new HashMap<>());
        var rootNode = new RootNode("root", KafkaTopics.Generic.COMPUTATION_TOPIC);
        var counterNode1 = new CounterNode("counterBeforeFilter", MetricsIndex.TEST_COUNTER_BEFORE_FILTER);
        var filterNode = new FilterNode("filter", FILTER_EXPRESSION);
        var counterNode2 = new CounterNode("counterAfterFilter", MetricsIndex.TEST_COUNTER_AFTER_FILTER);
        var sinkNode = new SinkNode("sink", influxConfig.getDefaultStreamsBucketName());

        val allNodes = List.of(rootNode, counterNode1, filterNode, counterNode2, sinkNode);

        topology.addEdge(rootNode, counterNode1, true);
        topology.addEdge(counterNode1, filterNode, true);
        topology.addEdge(filterNode, counterNode2, true);
        topology.addEdge(counterNode2, sinkNode, true);

        return new TopologySetupData(topology, allNodes);
    }

}
