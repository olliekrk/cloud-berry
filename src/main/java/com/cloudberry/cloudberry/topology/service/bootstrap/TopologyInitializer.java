package com.cloudberry.cloudberry.topology.service.bootstrap;


import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.TopologySetupData;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import com.cloudberry.cloudberry.topology.service.TopologyNodeService;
import com.cloudberry.cloudberry.topology.service.TopologyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TopologyInitializer {
    private final TopologyService topologyService;
    private final TopologyNodeService topologyNodeService;

    public Topology saveTopologySetupData(TopologySetupData topologySetupData) {
        var topology = saveTopology(topologySetupData.getTopology());
        saveNodes(topologySetupData.getNodes());
        return topology;
    }

    public void purgeDefaults() {
        var defaultTopologies = topologyService.findDefaultTopologies();
        topologyService.deleteAllByIds(ListSyntax.mapped(defaultTopologies, Topology::getId));
        topologyNodeService.deleteAllByIds(ListSyntax.flatMapped(defaultTopologies, Topology::getVertices));
    }

    private Topology saveTopology(Topology topology) {
        return topologyService.save(topology);
    }

    private void saveNodes(List<TopologyNode> nodes) {
        topologyNodeService.saveAll(nodes);
    }
}
