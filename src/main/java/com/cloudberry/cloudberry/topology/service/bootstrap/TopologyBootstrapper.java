package com.cloudberry.cloudberry.topology.service.bootstrap;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.processing.processor.ComputationEventProcessor;
import com.cloudberry.cloudberry.topology.exception.InvalidRootNodeException;
import com.cloudberry.cloudberry.topology.exception.NodeNotFoundException;
import com.cloudberry.cloudberry.topology.exception.TopologyException;
import com.cloudberry.cloudberry.topology.exception.bootstrap.BootstrappingException;
import com.cloudberry.cloudberry.topology.exception.bootstrap.MissingBootstrappingLogicException;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import com.cloudberry.cloudberry.topology.service.TopologyNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.jgrapht.traverse.DepthFirstIterator;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopologyBootstrapper {
    private final TopologyNodeService topologyNodeService;
    private final ComputationEventProcessor computationEventProcessor;

    private final StreamsBuilder kStreamsBuilder;
    private final KafkaStreamsConfiguration kStreamsConfiguration;

    public KafkaStreams configureStreams(Topology topology) throws TopologyException {
        log.info("Topology '" + topology.getName() + "' will now be used.");
        var rootId = topology.findRootId(); // todo: just a test, (consider having multiple root nodes)
        var rootNode = topologyNodeService
                .getNodeById(rootId)
                .map(node -> Optional.of(node)
                        .filter(n -> n instanceof RootNode)
                        .map(n -> (RootNode) n)
                        .orElseThrow(() -> new InvalidRootNodeException(node.getId()))
                )
                .orElseThrow(() -> new NodeNotFoundException(rootId));

        traverseAndConfigure(topology, rootNode);
        var streams = new KafkaStreams(kStreamsBuilder.build(), kStreamsConfiguration.asProperties());
        log.info("Topology '" + topology.getName() + "' has been configured.");
        return streams;
    }

    private void traverseAndConfigure(Topology topology, RootNode rootNode) {
        var context = new BootstrappingContext(topology);
        var topologyIterator = new DepthFirstIterator<>(topology.constructGraph(), rootNode.getId());
        topologyIterator.forEachRemaining(nodeId ->
                topologyNodeService.getNodeById(nodeId).ifPresent(node -> configureNode(context, node))
        );
    }

    // THE ESSENCE
    private void configureNode(BootstrappingContext context, TopologyNode node) throws BootstrappingException {
        if (node instanceof RootNode) {
            var rootNode = (RootNode) node;
            var inputTopic = rootNode.getInputTopicName();
            KStream<String, ComputationEvent> stream = kStreamsBuilder.stream(rootNode.getInputTopicName());
            context.addStream(inputTopic, stream);
        } else if (node instanceof SinkNode) {
            var sinkNode = (SinkNode) node;
            var inputTopic = sinkNode.getInputTopicName();
            var stream = context.getStreamOrThrow(inputTopic);
            stream.foreach((_key, event) -> computationEventProcessor.process(event, sinkNode.getOutputBucketName()));
            context.removeStream(inputTopic);
        } else {
            throw new MissingBootstrappingLogicException(node);
        }
    }

}
