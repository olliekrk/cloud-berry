package com.cloudberry.cloudberry.topology.model.nodes;

import com.cloudberry.cloudberry.topology.service.visitor.TopologyNodeVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TopologyNode")
@Data
@EqualsAndHashCode(callSuper = true)
public class MergeNode extends TopologyNode {

    public MergeNode(String name) {
        super(ObjectId.get(), name);
    }

    @Override
    public String getNodeType() {
        return TopologyNodeType.Merge.name();
    }

    @Override
    public void accept(TopologyNodeVisitor visitor) {
        visitor.visit(this);
    }
}
