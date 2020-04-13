package com.cloudberry.cloudberry.model.metadata;

import com.cloudberry.cloudberry.model.Parametrized;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "experiment")
public class Experiment implements Parametrized<String, Object> {
    @Id
    private Long id;
    @With
    @Indexed
    private String name;
    private Map<String, Object> parameters;
    private Instant time;

    public Experiment(Instant time, String name, Map<String, Object> parameters) {
        this.time = time;
        this.name = name;
        this.parameters = parameters;
    }
}
