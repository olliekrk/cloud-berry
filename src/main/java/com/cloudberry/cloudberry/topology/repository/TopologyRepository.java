package com.cloudberry.cloudberry.topology.repository;

import com.cloudberry.cloudberry.topology.model.Topology;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface TopologyRepository extends MongoRepository<Topology, ObjectId> {

    List<Topology> findByName(String name);

    void removeAllByIdIn(Collection<ObjectId> id);
}
