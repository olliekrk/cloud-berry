package com.cloudberry.cloudberry.model.metadata;

import com.cloudberry.cloudberry.model.Parametrized;
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
public class ExperimentConfiguration implements Parametrized<String, Object> {
    @Id
    private ObjectId id;
    @Indexed
    private Long experimentId;
    private String configurationFileName;
    private Map<String, Object> parameters;
    @CreatedDate
    private Date createdDate;

    public ExperimentConfiguration(Long experimentId, Map<String, Object> parameters) {
        this.experimentId = experimentId;
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
