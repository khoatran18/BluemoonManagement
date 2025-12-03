package com.project.resident_fee_service.exception;

public class BusinessException extends BaseException {
    public BusinessException(String message) {
        super(422, message);
    }
}
