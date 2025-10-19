package com.project.exception;

public class BusinessException extends BaseException {
    public BusinessException(String message) {
        super(422, message);
    }
}
