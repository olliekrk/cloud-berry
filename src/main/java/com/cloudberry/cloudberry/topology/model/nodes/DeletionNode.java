package com.cloudberry.cloudberry.topology.model.nodes;

import com.cloudberry.cloudberry.topology.model.deletion.DeletionExpression;
import com.cloudberry.cloudberry.topology.service.visitor.TopologyNodeVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TopologyNode")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeletionNode extends TopologyNode {

    private final DeletionExpression deletionExpression;

    public DeletionNode(String name, DeletionExpression deletionExpression) {
        super(ObjectId.get(), name);
        this.deletionExpression = deletionExpression;
    }

    @Override
    public String getNodeType() {
        return TopologyNodeType.Deletion.name();
    }

    @Override
    public void accept(TopologyNodeVisitor visitor) {
        visitor.visit(this);
    }
}
