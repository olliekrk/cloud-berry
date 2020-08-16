package com.cloudberry.cloudberry.db.mongo.data.metadata;

import com.cloudberry.cloudberry.model.Timed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
}
