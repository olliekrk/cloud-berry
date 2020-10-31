package com.cloudberry.cloudberry.topology.service.bootstrap;

import com.cloudberry.cloudberry.kafka.processing.processor.ComputationEventProcessor;
import com.cloudberry.cloudberry.metrics.MetricsRegistry;
import com.cloudberry.cloudberry.topology.exception.InvalidRootNodeException;
import com.cloudberry.cloudberry.topology.exception.MissingRootNodeException;
import com.cloudberry.cloudberry.topology.exception.TopologyException;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.bootstrap.BootstrappingContext;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.service.TopologyNodeService;
import com.cloudberry.cloudberry.topology.service.visitor.TopologyNodeBootstrappingVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopologyBootstrapper {
    private final TopologyNodeService topologyNodeService;
    private final ComputationEventProcessor computationEventProcessor;

    private final StreamsBuilder kStreamsBuilder;
    private final KafkaStreamsConfiguration kStreamsConfiguration;
    private final MetricsRegistry metricsRegistry;

    public KafkaStreams bootstrapStreams(@NotNull Topology topology) throws TopologyException {
        log.info("Topology '{}' bootstrapping has started.", topology.getName());

        var rootIds = topology.findRootIds();
        var rootNodes = rootIds.stream()
                .flatMap(nodeId -> topologyNodeService.getOpt(nodeId).stream())
                .map(node -> Optional.of(node)
                        .filter(n -> n instanceof RootNode)
                        .map(n -> (RootNode) n)
                        .orElseThrow(() -> new InvalidRootNodeException(node.getId()))
                )
                .collect(Collectors.toList());

        if (rootNodes.isEmpty()) {
            throw new MissingRootNodeException(topology.getId());
        }

        bootstrap(topology);
        var streams = new KafkaStreams(kStreamsBuilder.build(), kStreamsConfiguration.asProperties());
        log.info("Topology '" + topology.getName() + "' has been configured.");
        return streams;
    }

    private void bootstrap(Topology topology) {
        var visitor = new TopologyNodeBootstrappingVisitor(
                new BootstrappingContext(topology),
                kStreamsBuilder,
                computationEventProcessor,
                metricsRegistry
        );
        var graph = topology.constructGraph();
        graph.iterator().forEachRemaining(nodeId -> topologyNodeService.get(nodeId).accept(visitor));
    }

}
