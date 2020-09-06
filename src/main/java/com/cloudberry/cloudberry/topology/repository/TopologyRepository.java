package com.cloudberry.cloudberry.topology.repository;

import com.cloudberry.cloudberry.topology.model.Topology;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TopologyRepository extends MongoRepository<Topology, ObjectId> {
}
