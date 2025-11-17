package com.project.exception;

import com.project.exception.BaseException;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
