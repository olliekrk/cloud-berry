package com.cloudberry.cloudberry.rest.advice;

import lombok.Value;

@Value
public class ApiError {
    String error;
    int code;
}
