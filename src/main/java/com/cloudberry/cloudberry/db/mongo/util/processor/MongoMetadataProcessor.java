package com.cloudberry.cloudberry.db.mongo.util.processor;

import com.cloudberry.cloudberry.kafka.event.metadata.MetadataEvent;
import com.cloudberry.cloudberry.db.mongo.data.metadata.Experiment;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentConfiguration;
import com.cloudberry.cloudberry.db.mongo.data.metadata.ExperimentEvaluation;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class MongoMetadataProcessor {

    public Experiment extractExperimentData(MetadataEvent event) {
        return new Experiment(event.getTime(),
                event.getExperimentName(),
                event.getExperimentParameters());
    }

    public ExperimentConfiguration extractConfigurationData(MetadataEvent event, Experiment experiment) {
        return new ExperimentConfiguration(
                new ObjectId(),
                experiment.getTime(),
                experiment.getId(),
                event.getConfigurationParameters());
    }

    public ExperimentEvaluation extractEvaluationData(MetadataEvent event, ExperimentConfiguration configuration) {
        return new ExperimentEvaluation(event.getEvaluationId(),
                configuration.getId(),
                event.getTime());
    }
}
