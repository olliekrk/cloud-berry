package com.cloudberry.cloudberry.db.common.service.age.parsing;

import com.cloudberry.cloudberry.db.common.data.ImportDetails;
import com.cloudberry.cloudberry.db.common.data.ParsedLogs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsParser {
    private final SimpleLogsParser simpleLogsParser;
    private final WithIdLogsParser withIdLogsParser;

    public ParsedLogs parseLogs(File file, String experimentName, ImportDetails importDetails) throws IOException {
        var experimentRepresentation = simpleLogsParser.parseFile(file, importDetails);
        return withIdLogsParser.parseLogsWithId(experimentRepresentation, experimentName);
    }

}
