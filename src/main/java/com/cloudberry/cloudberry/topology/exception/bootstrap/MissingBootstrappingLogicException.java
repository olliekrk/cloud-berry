package com.cloudberry.cloudberry.topology.exception.bootstrap;

import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import org.springframework.http.HttpStatus;

public class MissingBootstrappingLogicException extends BootstrappingException {
    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public MissingBootstrappingLogicException(TopologyNode node) {
        super("No bootstrapping logic defined for node '" + node.getName() + "' with id " + node.getId(), STATUS);
    }
}
