package com.cloudberry.cloudberry.topology.model.nodes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TopologyNode")
@Data
@EqualsAndHashCode(callSuper = true)
public class RootNode extends TopologyNode {
    private String inputTopicName;

    public RootNode(String name, String inputTopicName) {
        super(ObjectId.get(), name);
        this.inputTopicName = inputTopicName;
    }
}