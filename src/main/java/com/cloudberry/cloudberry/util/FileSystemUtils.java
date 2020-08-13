package com.cloudberry.cloudberry.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.function.Function;

public abstract class FileSystemUtils {

    public static <R> R withTemporaryFile(MultipartFile file,
                                          Function<Path, R> temporaryFilePathAction) throws IOException {
        Path temporaryFilePath = null;
        try {
            temporaryFilePath = createTemporaryFile(file.getOriginalFilename());
            file.transferTo(temporaryFilePath);
            return temporaryFilePathAction.apply(temporaryFilePath);
        } finally {
            if (temporaryFilePath != null) {
                temporaryFilePath.toFile().delete();
            }
        }
    }

    private static Path createTemporaryFile(String originalFileName) throws IOException {
        var prefix = String.format("cloudberry_tmp_%d_", Instant.now().toEpochMilli());
        return Files.createTempFile(prefix, originalFileName);
    }
}
