package com.cloudberry.cloudberry.db.mongo.util;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Used to maintain sequential long IDs.
 */
@Data
@Document
public class IdSequence {
    @Id
    private String id;
    private long sequence;
}
