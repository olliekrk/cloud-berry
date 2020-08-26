package com.cloudberry.cloudberry.rest.util;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface RestParametersUtil {

    static Optional<ObjectId> getValidId(String rawId) {
        return Optional.of(rawId)
                .filter(ObjectId::isValid)
                .map(ObjectId::new);
    }

    static List<ObjectId> getValidIds(List<String> rawIds) {
        return ListSyntax.flatMappedOpt(rawIds, RestParametersUtil::getValidId);
    }



}
