package com.cloudberry.cloudberry.topology.service.visitor;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.processing.processor.ComputationEventProcessor;
import com.cloudberry.cloudberry.metrics.MetricsRegistry;
import com.cloudberry.cloudberry.topology.exception.bootstrap.BootstrappingException;
import com.cloudberry.cloudberry.topology.model.bootstrap.BootstrappingContext;
import com.cloudberry.cloudberry.topology.model.filtering.ExpressionChecker;
import com.cloudberry.cloudberry.topology.model.mapping.Mapper;
import com.cloudberry.cloudberry.topology.model.nodes.CounterNode;
import com.cloudberry.cloudberry.topology.model.nodes.FilterNode;
import com.cloudberry.cloudberry.topology.model.nodes.MapNode;
import com.cloudberry.cloudberry.topology.model.nodes.MergeNode;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.streams.KeyValue;
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
        var mergedStream = mergePredecessors(node);
        mergedStream.foreach((key, event) -> sinkProcessor.process(event, node.getOutputBucketName()));
    }

    @Override
    public void visit(CounterNode node) {
        var mergedStream = mergePredecessors(node);
        var peekedStream = mergedStream.peek((key, event) -> metricsRegistry.incrementCounter(node.getMetricName()));
        context.putStream(node.getId(), peekedStream);
    }

    @Override
    public void visit(FilterNode node) {
        var mergedStream = mergePredecessors(node);
        var filteredStream = mergedStream.filter((key, event) -> ExpressionChecker.check(event, node.getExpression()));
        context.putStream(node.getId(), filteredStream);
    }

    @Override
    public void visit(MapNode node) {
        var mergedStream = mergePredecessors(node);
        var mappedStream = mergedStream.map(((key, event) -> KeyValue.pair(
                key,
                Mapper.calculateNewComputationEvent(
                        event,
                        node.getMappingExpression()
                )
        )));
        context.putStream(node.getId(), mappedStream);
    }

    @Override
    public void visit(MergeNode node) {
        var mergedStream = mergePredecessors(node);
        context.putStream(node.getId(), mergedStream);
    }

    private KStream<String, ComputationEvent> mergePredecessors(TopologyNode node) {
        var predecessorsIds = context.getPredecessorNodesIds(node.getId());
        var mergedStreamOpt = predecessorsIds
                .stream()
                .map(context::getStreamOrThrow)
                .reduce(KStream::merge);

        predecessorsIds.forEach(context::removeStream);
        return mergedStreamOpt.orElseThrow(() -> new BootstrappingException("Missing stream!") {}); // should not happen
    }

}
