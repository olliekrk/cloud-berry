package com.cloudberry.cloudberry.db.mongo.data.metadata;

import com.cloudberry.cloudberry.common.trait.Parametrized;
import com.cloudberry.cloudberry.common.trait.Timed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ExperimentConfiguration implements Parametrized<String, Object>, Timed {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId experimentId;
    @Nullable
    @With
    private String configurationFileName;
    @With
    private Map<String, Object> parameters;
    private Instant time;
}


