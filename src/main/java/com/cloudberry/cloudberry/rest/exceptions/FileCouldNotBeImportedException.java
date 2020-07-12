package com.cloudberry.cloudberry.rest.exceptions;

public class FileCouldNotBeImportedException extends RestException {
    public FileCouldNotBeImportedException() {
        super("Failed to import file");
    }

    public FileCouldNotBeImportedException(Exception e) {
        super("Failed to import file", e);
    }
}
