package com.cloudberry.cloudberry.rest.util;

import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidTopologyIdException;
import com.cloudberry.cloudberry.rest.exceptions.invalid.id.InvalidTopologyNodeIdException;
import org.bson.types.ObjectId;

import java.util.List;

public class TopologyIdDispatcher {

    public static ObjectId getTopologyNodeId(String topologyNodeIdHex) throws InvalidTopologyNodeIdException {
        return RestParametersUtil.getValidId(topologyNodeIdHex)
                .orElseThrow(() -> new InvalidTopologyNodeIdException(List.of(topologyNodeIdHex)));
    }

    public static List<ObjectId> getTopologyNodesIds(List<String> topologyNodesIdsHex)
            throws InvalidTopologyNodeIdException {
        var topologyNodesIds = RestParametersUtil.getValidIds(topologyNodesIdsHex);
        if (topologyNodesIds.isEmpty()) { throw new InvalidTopologyNodeIdException(topologyNodesIdsHex); }
        return topologyNodesIds;
    }

    public static ObjectId getTopologyId(String topologyIdHex) throws InvalidTopologyIdException {
        return RestParametersUtil.getValidId(topologyIdHex)
                .orElseThrow(() -> new InvalidTopologyIdException(List.of(topologyIdHex)));
    }

    public static List<ObjectId> getTopologiesIds(List<String> topologiesIdsHex)
            throws InvalidTopologyIdException {
        var topologiesIds = RestParametersUtil.getValidIds(topologiesIdsHex);
        if (topologiesIds.isEmpty()) { throw new InvalidTopologyIdException(topologiesIdsHex); }
        return topologiesIds;
    }
}
