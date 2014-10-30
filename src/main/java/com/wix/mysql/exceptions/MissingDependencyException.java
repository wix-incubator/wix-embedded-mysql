package com.wix.mysql.exceptions;

public class MissingDependencyException extends RuntimeException {
    public MissingDependencyException(String message) {
        super(message);
    }
}
