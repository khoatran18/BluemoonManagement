package com.project.resident_fee_service.exception;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
