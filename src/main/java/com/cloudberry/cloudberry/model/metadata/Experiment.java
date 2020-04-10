package com.cloudberry.cloudberry.model.metadata;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "experiment")
public class Experiment {
    @Id
    private ObjectId id;
    private String title;
    @CreatedDate
    private Date createdDate;
}
