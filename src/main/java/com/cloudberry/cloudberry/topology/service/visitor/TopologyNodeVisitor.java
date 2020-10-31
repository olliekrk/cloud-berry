package com.cloudberry.cloudberry.topology.service.visitor;

import com.cloudberry.cloudberry.topology.model.nodes.CounterNode;
import com.cloudberry.cloudberry.topology.model.nodes.RootNode;
import com.cloudberry.cloudberry.topology.model.nodes.SinkNode;

public interface TopologyNodeVisitor {

    void visit(RootNode node);

    void visit(SinkNode node);

    void visit(CounterNode node);
}
