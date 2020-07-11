package com.cloudberry.cloudberry.db.mongo.data.metadata;

import com.cloudberry.cloudberry.model.Parametrized;
import com.cloudberry.cloudberry.model.Timed;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document
public class ExperimentConfiguration implements Parametrized<String, Object>, Timed {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId experimentId;
    private String configurationFileName;
    private Map<String, Object> parameters;
    private Instant time;

    public ExperimentConfiguration(ObjectId id, Instant time, ObjectId experimentId, Map<String, Object> parameters) {
        this.id = id;
        this.time = time;
        this.experimentId = experimentId;
        this.parameters = parameters;
    }
}
