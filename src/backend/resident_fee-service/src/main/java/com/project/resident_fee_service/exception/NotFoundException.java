package com.project.resident_fee_service.exception;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(404, message);
    }
}
