package com.cloudberry.cloudberry.db.common.service;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;

public interface LogsParser<T> {
    T parseExperimentFile(File file, ObjectId experimentId, ImportDetails importDetails) throws IOException;
}
