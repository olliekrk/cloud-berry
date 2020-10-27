package com.cloudberry.cloudberry.topology.model;

import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import lombok.Value;

import java.util.List;

@Value
public class TopologySetupData {
    Topology topology;
    List<TopologyNode> nodes;
}
