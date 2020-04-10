package com.cloudberry.cloudberry.model.logs;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "logs")
public abstract class Log {
    @Id
    protected ObjectId id;
}
