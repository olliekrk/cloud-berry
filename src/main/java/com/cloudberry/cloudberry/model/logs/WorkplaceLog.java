package com.cloudberry.cloudberry.model.logs;

import com.cloudberry.cloudberry.model.Parametrized;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * W log from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Value
@Document(collection = "workplace_log")
public class WorkplaceLog extends Log implements Parametrized<String, Object> {
    @Indexed
    long workplaceId;
    Map<String, Object> parameters;

    public WorkplaceLog(Instant time, UUID evaluationId, long workplaceId, Map<String, Object> parameters) {
        super(time, evaluationId);
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }
}
