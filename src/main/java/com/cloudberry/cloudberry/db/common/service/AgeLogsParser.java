package com.cloudberry.cloudberry.db.common.service;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;

import java.io.File;
import java.io.IOException;

public interface AgeLogsParser<T> {
    T parseExperimentFile(File file, String experimentName, ImportDetails importDetails) throws IOException;
}
