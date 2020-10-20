package com.cloudberry.cloudberry.util;

import io.vavr.control.Try;

import java.io.File;
import java.nio.file.Paths;


public class FilesUtils {
    public static File getFileFromResources(String fileName) {
        return Try.of(() ->
                              Paths.get(FilesUtils.class.getResource(fileName).toURI())
                                      .toFile()
        )
                .getOrElseThrow(e -> new IllegalArgumentException("file is not found!"));
    }
}
