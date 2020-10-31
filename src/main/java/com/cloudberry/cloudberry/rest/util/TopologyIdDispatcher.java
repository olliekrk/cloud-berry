package com.cloudberry.cloudberry.rest.util;

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
        var experimentIds = RestParametersUtil.getValidIds(topologyNodesIdsHex);
        if (experimentIds.isEmpty()) { throw new InvalidTopologyNodeIdException(topologyNodesIdsHex); }
        return experimentIds;
    }
}
