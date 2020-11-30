package com.cloudberry.cloudberry.topology.service.visitor;

import com.cloudberry.cloudberry.topology.model.nodes.BranchNode;
import com.cloudberry.cloudberry.topology.model.nodes.CounterNode;
import com.cloudberry.cloudberry.topology.model.nodes.FilterNode;
import com.cloudberry.cloudberry.topology.model.nodes.MapNode;
import com.cloudberry.cloudberry.topology.model.nodes.MergeNode;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;

public interface TopologyNodeVisitor {

    void visit(RootNode node);

    void visit(SinkNode node);

    void visit(CounterNode node);

    void visit(FilterNode node);

    void visit(MapNode node);

    void visit(MergeNode node);

    void visit(BranchNode branchNode);
}
