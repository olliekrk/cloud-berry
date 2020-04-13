package com.cloudberry.cloudberry.converters;

import com.cloudberry.cloudberry.model.event.MetadataEvent;
import com.cloudberry.cloudberry.model.metadata.Experiment;
import com.cloudberry.cloudberry.model.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.model.metadata.ExperimentEvaluation;
import org.springframework.stereotype.Component;

@Component
public class MetadataConverter {
    
    public Experiment toExperiment(MetadataEvent event) {
        return new Experiment(event.getTime(),
                event.getName(),
                event.getExperimentParameters());
    }

    public ExperimentConfiguration toConfiguration(MetadataEvent event, Experiment experiment) {
        return new ExperimentConfiguration(experiment.getTime(),
                experiment.getId(),
                event.getConfigurationParameters());
    }

    public ExperimentEvaluation toEvaluation(MetadataEvent event, ExperimentConfiguration configuration) {
        return new ExperimentEvaluation(event.getEvaluationId(),
                configuration.getId(),
                event.getTime());
    }
}
