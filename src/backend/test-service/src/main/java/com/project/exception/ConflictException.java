package com.project.exception;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(409, message);
    }
}
