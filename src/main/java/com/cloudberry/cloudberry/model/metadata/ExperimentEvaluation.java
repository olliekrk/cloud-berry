package com.cloudberry.cloudberry.model.metadata;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "experiment_evaluation")
public class ExperimentEvaluation {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId configurationId;
    private Instant evaluationTime;
}
