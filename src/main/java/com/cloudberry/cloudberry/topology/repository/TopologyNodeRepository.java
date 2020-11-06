package com.cloudberry.cloudberry.topology.repository;

import com.cloudberry.cloudberry.topology.model.nodes.TopologyNode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface TopologyNodeRepository extends MongoRepository<TopologyNode, ObjectId> {

    List<TopologyNode> findAllByName(String name);

    List<TopologyNode> findAllByIdIn(Collection<ObjectId> ids);

    List<TopologyNode> deleteAllByIdIn(Collection<ObjectId> ids);

}
