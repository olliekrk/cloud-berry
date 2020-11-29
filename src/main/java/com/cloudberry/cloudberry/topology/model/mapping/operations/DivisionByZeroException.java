package com.cloudberry.cloudberry.topology.model.mapping.operations;

public class DivisionByZeroException extends IllegalArgumentException {
    public DivisionByZeroException() {
        super("Cannot divide by 0!");
    }
}
