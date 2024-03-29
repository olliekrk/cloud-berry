package com.cloudberry.cloudberry.topology.service.visitor;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.kafka.processing.processor.ComputationEventProcessor;
import com.cloudberry.cloudberry.metrics.MetricsRegistry;
import com.cloudberry.cloudberry.topology.exception.bootstrap.BootstrappingException;
import com.cloudberry.cloudberry.topology.model.bootstrap.BootstrappingContext;
import com.cloudberry.cloudberry.topology.model.branching.BranchOutput;
import com.cloudberry.cloudberry.topology.model.deletion.DeletionEvaluator;
import com.cloudberry.cloudberry.topology.model.filtering.ExpressionChecker;
import com.cloudberry.cloudberry.topology.model.mapping.MappingNodeEvaluator;
import com.cloudberry.cloudberry.topology.model.nodes.BranchNode;
import com.cloudberry.cloudberry.topology.model.nodes.CounterNode;
import com.cloudberry.cloudberry.topology.model.nodes.DeletionNode;
import com.cloudberry.cloudberry.topology.model.nodes.FilterNode;
import com.cloudberry.cloudberry.topology.model.nodes.MapNode;
import com.cloudberry.cloudberry.topology.model.nodes.MergeNode;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import io.vavr.control.Try;
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
    private final MappingNodeEvaluator mappingNodeEvaluator;

    @Override
    public void visit(RootNode node) {
        var inputTopic = node.getInputTopicName();
        KStream<String, ComputationEvent> inputStream = streamsBuilder.stream(inputTopic);
        context.putStream(node.getId(), inputStream);
    }

    @Override
    public void visit(SinkNode node) {
        var mergedStream = mergeIncomingEdges(node);
        mergedStream.foreach((key, event) -> sinkProcessor.process(event, node.getOutputBucketName()));
    }

    @Override
    public void visit(CounterNode node) {
        var mergedStream = mergeIncomingEdges(node);
        var peekedStream = mergedStream.peek((key, event) -> metricsRegistry.incrementCounter(node.getMetricName()));
        context.putStream(node.getId(), peekedStream);
    }

    @Override
    public void visit(FilterNode node) {
        var mergedStream = mergeIncomingEdges(node);
        var filteredStream = mergedStream.filter((key, event) -> ExpressionChecker.check(event, node.getExpression()));
        context.putStream(node.getId(), filteredStream);
    }

    @Override
    public void visit(MapNode node) {
        var mergedStream = mergeIncomingEdges(node);
        var mappedStream = mergedStream.map(((key, event) -> KeyValue.pair(
                key,
                Try.of(() -> mappingNodeEvaluator.calculateNewComputationEvent(
                        event,
                        node.getMappingExpression()
                )).get()
        )));
        context.putStream(node.getId(), mappedStream);
    }

    @Override
    public void visit(MergeNode node) {
        var mergedStream = mergeIncomingEdges(node);
        context.putStream(node.getId(), mergedStream);
    }

    @Override
    public void visit(BranchNode node) {
        var mergedStream = mergeIncomingEdges(node);
        @SuppressWarnings("unchecked")
        var branchedStreams = mergedStream.branch(
                (key, event) -> ExpressionChecker.check(event, node.getExpression()), // first stream
                (key, event) -> true // second stream (matches all the rest)
        );

        context.putStream(node.getId(), BranchOutput.MATCHED.name(), branchedStreams[0]);
        context.putStream(node.getId(), BranchOutput.UNMATCHED.name(), branchedStreams[1]);
    }

    @Override
    public void visit(DeletionNode deletionNode) {
        var mergedStream = mergeIncomingEdges(deletionNode);
        var mappedStream = mergedStream.map(((key, event) -> KeyValue.pair(
                key,
                DeletionEvaluator.calculateNewComputationEvent(event, deletionNode.getDeletionExpression())
        )));
        context.putStream(deletionNode.getId(), mappedStream);
    }

    private KStream<String, ComputationEvent> mergeIncomingEdges(TopologyNode node) {
        var incomingEdges = context.getIncomingEdges(node.getId());

        var mergedStreamOpt = incomingEdges
                .stream()
                .map(edge -> context.getStreamOrThrow(edge.getSource(), edge.getName()))
                .reduce(KStream::merge);

        incomingEdges.forEach(edge -> context.removeStream(edge.getSource(), edge.getName()));
        return mergedStreamOpt.orElseThrow(() -> new BootstrappingException("Missing stream!") {}); // should not happen
    }

}
