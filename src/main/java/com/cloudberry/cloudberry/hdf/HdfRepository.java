package com.cloudberry.cloudberry.hdf;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface HdfRepository extends ReactiveMongoRepository<HdfFile, ObjectId> {
}
