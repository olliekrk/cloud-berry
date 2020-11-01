package com.cloudberry.cloudberry.properties.model;

import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
@Value
public class ApiProperty {
    @MongoId
    String id;
    String value;
}
