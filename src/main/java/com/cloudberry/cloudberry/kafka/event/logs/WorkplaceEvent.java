package com.cloudberry.cloudberry.kafka.event.logs;

import com.cloudberry.cloudberry.common.trait.Parametrized;
import com.cloudberry.cloudberry.kafka.event.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.Map;

@Getter
public class WorkplaceEvent extends Event implements Parametrized<String, Object> {

    public interface Fields {
        String WORKPLACE_ID = "workplaceId";
    }

    private final ObjectId computationId;
    private final long workplaceId;
    private final Map<String, Object> parameters;

    public WorkplaceEvent(ObjectId computationId, long workplaceId, Map<String, Object> parameters) {
        this.computationId = computationId;
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @JsonCreator
    private WorkplaceEvent(@JsonProperty("computationId") ObjectId computationId,
                           @JsonProperty("time") Instant time,
                           @JsonProperty("workplaceId") long workplaceId,
                           @JsonProperty("parameters") Map<String, Object> parameters) {
        super(time);
        this.computationId = computationId;
        this.workplaceId = workplaceId;
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

}
