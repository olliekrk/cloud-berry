package com.cloudberry.cloudberry.db.mongo.data.metadata;

import com.cloudberry.cloudberry.model.Parametrized;
import com.cloudberry.cloudberry.model.Timed;
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
}
