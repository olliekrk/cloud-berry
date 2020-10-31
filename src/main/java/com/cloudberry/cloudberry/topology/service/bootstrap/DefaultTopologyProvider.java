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
import static com.cloudberry.cloudberry.topology.model.operators.LogicalOperator.OR;

@Component
@RequiredArgsConstructor
public class DefaultTopologyProvider {
    private final InfluxConfig influxConfig;
    private final static String DEFAULT_TOPOLOGY_NAME = AppConstants.APPLICATION_NAME + " (Default)";
    private static final FilterExpression FILTER_EXPRESSION = new FilterExpression(
            AND,
            List.of(new FilterExpression(OR, List.of(),
                                         List.of(new FilterPredicate("predicateName", "100", EqualityOperator.EQ,
                                                                     FilterType.NUMERIC, true
                                         ))
            )),
            List.of(new FilterPredicate("top predicate", "true", EqualityOperator.NEQ,
                                        FilterType.BOOLEAN, false
            ))
    );

    public TopologySetupData get() {
        var topology = new Topology(ObjectId.get(), DEFAULT_TOPOLOGY_NAME, false, new HashMap<>());
        var rootNode = new RootNode("root", KafkaTopics.Generic.COMPUTATION_TOPIC);
        var counterNode1 = new CounterNode("counterBeforeFilter", MetricsIndex.COUNTER_NODE_TEST_1);
        var filterNode = new FilterNode("filter", FILTER_EXPRESSION);
        var counterNode2 = new CounterNode("counterAfterFilter", MetricsIndex.COUNTER_NODE_TEST_2);
        var sinkNode = new SinkNode("sink", influxConfig.getDefaultStreamsBucketName());

        val allNodes = List.of(rootNode, counterNode1, filterNode, counterNode2, sinkNode);

        allNodes.forEach(topology::addVertex);

        topology.addEdge(rootNode, counterNode1);
        topology.addEdge(counterNode1, filterNode);
        topology.addEdge(filterNode, counterNode2);
        topology.addEdge(counterNode2, sinkNode);

        return new TopologySetupData(topology, allNodes);
    }

}
