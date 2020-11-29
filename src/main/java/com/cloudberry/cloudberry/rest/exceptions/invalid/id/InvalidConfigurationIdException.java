package com.cloudberry.cloudberry.rest.exceptions.invalid.id;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.rest.exceptions.RestException;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class InvalidConfigurationIdException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public InvalidConfigurationIdException(List<String> ids) {
        super("None of " + Arrays.toString(ids.toArray()) + " is a valid configuration id", STATUS);
    }

    public static InvalidConfigurationIdException empty(List<ObjectId> ids) {
        return new InvalidConfigurationIdException(ListSyntax.mapped(ids, ObjectId::toHexString));
    }
}
