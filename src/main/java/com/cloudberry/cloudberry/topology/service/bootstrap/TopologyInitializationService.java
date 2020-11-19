package com.cloudberry.cloudberry.topology.service.bootstrap;


import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.properties.ApiPropertiesService;
import com.cloudberry.cloudberry.properties.model.CloudberryPropertyId;
import com.cloudberry.cloudberry.topology.model.Topology;
import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import com.cloudberry.cloudberry.topology.service.TopologyNodeService;
import com.cloudberry.cloudberry.topology.service.TopologyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopologyInitializationService {
    private final TopologyService topologyService;
    private final TopologyNodeService topologyNodeService;
    private final DefaultTopologyProvider defaultTopologyProvider;
    private final ApiPropertiesService apiPropertiesService;

    public Topology getInitialTopology() {
//         purgeDefaults();

        var defaultTopologyId = apiPropertiesService
                .get(CloudberryPropertyId.OVERRIDDEN_DEFAULT_TOPOLOGY_ID)
                .filter(ObjectId::isValid)
                .map(ObjectId::new);

        return defaultTopologyId
                .flatMap(topologyService::findById)
                .filter(Topology::isValid)
                .orElseGet(() -> {
                    log.warn("No overridden default valid topology was found");
                    apiPropertiesService.reset(CloudberryPropertyId.OVERRIDDEN_DEFAULT_TOPOLOGY_ID);
                    log.warn("Retrying bootstrapping with predefined default topology");
                    var defaults = defaultTopologyProvider.get();
                    saveNodes(defaults.getNodes());
                    return saveTopology(defaults.getTopology());
                });
    }

    private void purgeDefaults() {
        var defaultTopologies = topologyService.findDefaultTopologies();
        topologyService.deleteAllByIds(ListSyntax.mapped(defaultTopologies, Topology::getId));
        topologyNodeService.deleteAllByIds(ListSyntax.flatMapped(defaultTopologies, Topology::getVertices));
    }

    private void saveNodes(List<TopologyNode> nodes) {
        topologyNodeService.saveAll(nodes);
    }

    private Topology saveTopology(Topology topology) {
        return topologyService.save(topology);
    }
}
