package com.cloudberry.cloudberry.topology.model.nodes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TopologyNode")
@Data
@EqualsAndHashCode(callSuper = true)
public class SinkNode extends TopologyNode {
    private String inputTopicName;
    private String outputBucketName;

    public SinkNode(String name, String inputTopicName, String outputBucketName) {
        super(ObjectId.get(), name);
        this.inputTopicName = inputTopicName;
        this.outputBucketName = outputBucketName;
    }
}
