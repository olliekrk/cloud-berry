package com.cloudberry.cloudberry.topology.model.nodes;

import com.cloudberry.cloudberry.topology.model.filtering.FilterExpression;
import com.cloudberry.cloudberry.topology.service.visitor.TopologyNodeVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TopologyNode")
@Data
@EqualsAndHashCode(callSuper = true)
public class BranchNode extends TopologyNode {
    private final FilterExpression expression;

    public BranchNode(String name, FilterExpression expression) {
        super(ObjectId.get(), name);
        this.expression = expression;
    }

    @Override
    public String getNodeType() {
        return TopologyNodeType.Branch.name();
    }

    @Override
    public void accept(TopologyNodeVisitor visitor) {
        visitor.visit(this);
    }
}
