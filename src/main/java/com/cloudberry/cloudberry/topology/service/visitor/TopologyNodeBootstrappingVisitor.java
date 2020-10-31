package com.cloudberry.cloudberry.topology.service.visitor;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.processing.processor.ComputationEventProcessor;
import com.cloudberry.cloudberry.metrics.MetricsRegistry;
import com.cloudberry.cloudberry.topology.model.bootstrap.BootstrappingContext;
import com.cloudberry.cloudberry.topology.model.nodes.CounterNode;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

@AllArgsConstructor
public class TopologyNodeBootstrappingVisitor implements TopologyNodeVisitor {
    @Getter
    private final BootstrappingContext context;
    private final StreamsBuilder streamsBuilder;
    private final ComputationEventProcessor sinkProcessor;
    private final MetricsRegistry metricsRegistry;

    @Override
    public void visit(RootNode node) {
        var inputTopic = node.getInputTopicName();
        KStream<String, ComputationEvent> inputStream = streamsBuilder.stream(inputTopic);
        context.putStream(node.getId(), inputStream);
    }

    @Override
    public void visit(SinkNode node) {
        var predecessorsIds = context.getPredecessorNodesIds(node.getId());
        var mergedStreamOpt = predecessorsIds
                .stream()
                .map(context::getStreamOrThrow)
                .reduce(KStream::merge);

        predecessorsIds.forEach(context::removeStream);
        mergedStreamOpt.ifPresent(stream -> stream
                .foreach((key, event) -> sinkProcessor.process(event, node.getOutputBucketName()))
        );
    }

    @Override
    public void visit(CounterNode node) {
        // CounterNode assumes that there is has only 1 incoming predecessor's edge.
        context.getPredecessorNodesIds(node.getId())
                .stream()
                .findFirst()
                .ifPresent(pId -> {
                    var pStream = context.getStreamOrThrow(pId);
                    var pStreamPeeked = pStream.peek(
                            (_key, _event) -> metricsRegistry.incrementCounter(node.getMetricName())
                    );

                    context.putStream(pId, pStreamPeeked);
                    context.putStream(node.getId(), pStreamPeeked);
                });
    }

}
