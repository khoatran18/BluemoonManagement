package com.project.resident_fee_service.exception;

public class InternalServerException extends BaseException {
    public InternalServerException(String message) {
        super(500, message);
    }
}
