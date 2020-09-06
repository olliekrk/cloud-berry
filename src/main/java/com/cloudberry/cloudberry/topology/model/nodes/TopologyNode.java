package com.cloudberry.cloudberry.topology.model.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * Generic marker interface representing single element of Kafka Streams topology configuration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class TopologyNode {

    @Id
    protected ObjectId id;

    protected String name;

}
