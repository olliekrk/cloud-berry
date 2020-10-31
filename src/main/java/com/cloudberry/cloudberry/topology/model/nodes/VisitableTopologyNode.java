package com.cloudberry.cloudberry.topology.model.nodes;

import com.cloudberry.cloudberry.topology.service.visitor.TopologyNodeVisitor;

public interface VisitableTopologyNode {
    void accept(TopologyNodeVisitor visitor);
}
