package com.cloudberry.cloudberry.converters;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.model.metadata.Experiment;
import com.cloudberry.cloudberry.model.metadata.ExperimentConfiguration;

public interface ToMetadataConverter {
    Experiment convert(ProblemDefinitionEvent event);

    ExperimentConfiguration convert(Experiment experiment, ProblemDefinitionEvent event);
}
