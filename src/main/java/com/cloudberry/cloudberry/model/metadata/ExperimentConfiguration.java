package com.cloudberry.cloudberry.model.metadata;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@Document(collection = "experiment_configuration")
public class ExperimentConfiguration {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId experimentId;
    private String configurationFileName;
    private Map<String, Object> configuration;
    @CreatedDate
    private Date createdDate;
}
