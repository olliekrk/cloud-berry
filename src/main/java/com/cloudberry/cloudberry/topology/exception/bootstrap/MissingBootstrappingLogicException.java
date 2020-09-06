package com.cloudberry.cloudberry.topology.exception.bootstrap;

import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;

public class MissingBootstrappingLogicException extends BootstrappingException {
    public MissingBootstrappingLogicException(TopologyNode node) {
        super("No bootstrapping logic defined for node '" + node.getName() + "' with id " + node.getId());
    }
}
