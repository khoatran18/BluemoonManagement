package com.project.common_package.exception;

public class UnauthorizedException extends BaseException{
    public UnauthorizedException(String message) {
        super(401, message);
    }
}
