package com.project.common_package.exception;

public class BusinessException extends BaseException {
    public BusinessException(String message) {
        super(422, message);
    }
}
