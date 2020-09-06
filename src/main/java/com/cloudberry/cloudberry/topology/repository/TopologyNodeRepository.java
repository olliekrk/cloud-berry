package com.cloudberry.cloudberry.topology.repository;

import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TopologyNodeRepository extends MongoRepository<TopologyNode, ObjectId> {
}
