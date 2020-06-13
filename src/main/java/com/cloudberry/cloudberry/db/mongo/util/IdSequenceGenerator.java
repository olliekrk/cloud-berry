package com.cloudberry.cloudberry.db.mongo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdSequenceGenerator {

    private static final String idRef = "_id";
    private static final String seqRef = "sequence";
    final MongoOperations mongoOperations;

    public long nextId(String sequenceName) {
        IdSequence sequence = mongoOperations.findAndModify(
                Query.query(Criteria.where(idRef).is(sequenceName)),
                new Update().inc(seqRef, 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                IdSequence.class
        );

        return sequence == null ? 1 : sequence.getSequence();
    }
}
