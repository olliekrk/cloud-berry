package com.cloudberry.cloudberry.db.mongo.data.metadata;

import com.cloudberry.cloudberry.common.trait.Parametrized;
import com.cloudberry.cloudberry.common.trait.Timed;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Map;

@Data
@Document
@AllArgsConstructor
public class ExperimentConfiguration implements Parametrized<String, Object>, Timed {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId experimentId;
    @Nullable
    private String configurationFileName;
    private Map<String, Object> parameters;
    private Instant time;

    public ExperimentConfiguration(ObjectId experimentId, @Nullable String configurationFileName, Map<String, Object> parameters, Instant time) {
        this.id = new ObjectId();
        this.experimentId = experimentId;
        this.configurationFileName = configurationFileName;
        this.parameters = parameters;
        this.time = time;
    }
}
