package com.project.common_package.exception;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(404, message);
    }
}
