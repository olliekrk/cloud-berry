package com.cloudberry.cloudberry.db.mongo.data.metadata;

import com.cloudberry.cloudberry.model.Parametrized;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@Document(collection = "experiment_configuration")
public class ExperimentConfiguration implements Parametrized<String, Object> {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId experimentId;
    private String configurationFileName;
    private Map<String, Object> parameters;
    private Instant time;

    public ExperimentConfiguration(Instant time, ObjectId experimentId, Map<String, Object> parameters) {
        this.time = time;
        this.experimentId = experimentId;
        this.parameters = parameters;
    }
}
