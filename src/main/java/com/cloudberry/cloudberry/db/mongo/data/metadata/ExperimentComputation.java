package com.cloudberry.cloudberry.db.mongo.data.metadata;

import com.cloudberry.cloudberry.common.trait.Timed;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@Document
public class ExperimentComputation implements Timed {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId configurationId;
    private Instant time;

    public ExperimentComputation(ObjectId configurationId, Instant time) {
        this.id = new ObjectId();
        this.configurationId = configurationId;
        this.time = time;
    }
}
