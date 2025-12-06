package com.project.common_package.exception;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(409, message);
    }
}
