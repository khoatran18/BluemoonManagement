package main.java.com.project.exception;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
