package com.cloudberry.cloudberry.converters;

import com.cloudberry.cloudberry.model.event.ProblemDefinitionEvent;
import com.cloudberry.cloudberry.model.metadata.Experiment;
import com.cloudberry.cloudberry.model.metadata.ExperimentConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleToMetadataConverter implements ToMetadataConverter {
    @Override
    public Experiment convert(ProblemDefinitionEvent event) {
        return new Experiment(event.getTime(),
                event.getName(),
                event.getExperimentParameters());
    }

    @Override
    public ExperimentConfiguration convert(Experiment experiment, ProblemDefinitionEvent event) {
        return new ExperimentConfiguration(experiment.getTime(),
                experiment.getId(),
                event.getConfigurationParameters());
    }
}
