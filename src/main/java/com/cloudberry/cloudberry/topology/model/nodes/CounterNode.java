package com.cloudberry.cloudberry.topology.model.nodes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TopologyNode")
@Data
@EqualsAndHashCode(callSuper = true)
public class CounterNode extends TopologyNode {
    private String inputTopicName;
    private String counterName;

    public CounterNode(String name, String inputTopicName, String counterName) {
        super(ObjectId.get(), name);
        this.inputTopicName = inputTopicName;
        this.counterName = counterName;
    }
}
