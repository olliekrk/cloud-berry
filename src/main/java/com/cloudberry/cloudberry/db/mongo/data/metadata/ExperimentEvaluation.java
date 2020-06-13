package com.cloudberry.cloudberry.db.mongo.data.metadata;

import com.cloudberry.cloudberry.db.common.Timed;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@Document(collection = "experiment_evaluation")
public class ExperimentEvaluation implements Timed {
    @MongoId(FieldType.STRING)
    private UUID id;
    @Indexed
    private ObjectId configurationId;
    private Instant time;
}