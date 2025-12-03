package com.project.resident_fee_service.exception;

public class ApiResponse<T> {

    public boolean success;
    public int code;
    public String message;
    public T data;

    public ApiResponse(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<T>(true, 200, "Success", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, "Created success", data);
    }

    public static <T> ApiResponse<T> noContent(String message) {
        return new ApiResponse<T>(true, 204, "Deleted success", null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

}
