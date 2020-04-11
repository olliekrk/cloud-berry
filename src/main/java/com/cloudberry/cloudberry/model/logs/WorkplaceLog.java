package com.cloudberry.cloudberry.model.logs;

import com.cloudberry.cloudberry.model.Parametrized;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * W log from AgE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "workplace_log")
public class WorkplaceLog extends Log implements Parametrized<String, Object> {
    @Indexed
    private long workplaceId;
    private Map<String, Object> parameters;

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
