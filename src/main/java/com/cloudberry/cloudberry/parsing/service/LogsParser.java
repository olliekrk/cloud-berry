package com.cloudberry.cloudberry.parsing.service;

import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.cloudberry.cloudberry.parsing.model.UploadDetails;
import io.vavr.control.Try;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

public interface LogsParser<D extends UploadDetails> {

    String TIME_COLUMN_NAME = "TIME";

    ParsedLogs parseFile(File file, D details, String defaultMeasurementName) throws IOException;

    default Instant parseTime(@Nullable String timeValue) {
        return Try.of(
                () -> Optional.ofNullable(timeValue)
                        .map(Long::parseLong)
                        .map(Instant::ofEpochMilli)
                        .orElse(Instant.EPOCH)
        ).getOrElse(() -> Instant.EPOCH);
    }

    default Object parseField(String value) {
        return Try.of(() -> (Object) Double.parseDouble(value)).getOrElse(value);
    }
}
