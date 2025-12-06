package com.project.common_package.exception;

public class BaseException extends RuntimeException {

    private final int code;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
