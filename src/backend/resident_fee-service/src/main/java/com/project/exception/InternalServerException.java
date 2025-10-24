package main.java.com.project.exception;

public class InternalServerException extends BaseException {
    public InternalServerException(String message) {
        super(500, message);
    }
}
