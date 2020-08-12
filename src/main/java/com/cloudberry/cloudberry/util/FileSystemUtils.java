package com.cloudberry.cloudberry.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.function.Consumer;

public abstract class FileSystemUtils {

    public static boolean withTemporaryFile(MultipartFile file,
                                            Consumer<Path> temporaryFilePathAction) throws IOException {
        Path temporaryFilePath = null;
        var ok = true;
        try {
            temporaryFilePath = createTemporaryFile(file.getOriginalFilename());
            file.transferTo(temporaryFilePath);
            temporaryFilePathAction.accept(temporaryFilePath);
        } finally {
            if (temporaryFilePath != null) {
                ok = temporaryFilePath.toFile().delete();
            }
        }

        return ok;
    }

    private static Path createTemporaryFile(String originalFileName) throws IOException {
        var prefix = String.format("cloudberry_tmp_%d_", Instant.now().toEpochMilli());
        return Files.createTempFile(prefix, originalFileName);
    }
}
