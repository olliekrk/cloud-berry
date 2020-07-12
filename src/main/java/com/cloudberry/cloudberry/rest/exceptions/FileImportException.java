package com.cloudberry.cloudberry.rest.exceptions;

import org.springframework.http.HttpStatus;

public class FileImportException extends RestException {
    public static final String message = "Failed to import requested file";
    public static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public FileImportException(Throwable cause) {
        super(message, cause, status);
    }
}
