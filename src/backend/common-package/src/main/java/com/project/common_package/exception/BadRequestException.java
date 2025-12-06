package com.project.common_package.exception;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
