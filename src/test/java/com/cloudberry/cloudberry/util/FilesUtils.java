package com.cloudberry.cloudberry.util;

import java.io.File;
import java.net.URL;

public class FilesUtils {
    public static File getFileFromResources(String fileName) {
        ClassLoader classLoader = FilesUtils.class.getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }
}
