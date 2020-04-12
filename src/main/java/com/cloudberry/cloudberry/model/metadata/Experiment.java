package com.cloudberry.cloudberry.model.metadata;

import com.cloudberry.cloudberry.model.Parametrized;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
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
    @Getter
    private Map<String, Object> parameters;
    @CreatedDate
    private Date createdDate;

    public Experiment(String name, Map<String, Object> parameters) {
        this.name = name;
        this.parameters = parameters;
    }
}
