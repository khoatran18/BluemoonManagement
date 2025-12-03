package com.project.resident_fee_service.exception;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(409, message);
    }
}
