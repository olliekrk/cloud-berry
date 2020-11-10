package com.cloudberry.cloudberry.topology.model.nodes;

import com.cloudberry.cloudberry.topology.model.mapping.MappingExpression;
import com.cloudberry.cloudberry.topology.service.visitor.TopologyNodeVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TopologyNode")
@Data
@EqualsAndHashCode(callSuper = true)
public class MapNode extends TopologyNode {

    private final MappingExpression mappingExpression;

    public MapNode(String name, MappingExpression mappingExpression) {
        super(ObjectId.get(), name);
        this.mappingExpression = mappingExpression;
    }

    @Override
    public String getNodeType() {
        return TopologyNodeType.Map.name();
    }

    @Override
    public void accept(TopologyNodeVisitor visitor) {
        visitor.visit(this);
    }
}
