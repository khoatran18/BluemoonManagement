package com.project.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable e) {

        // My custom exception
        if (e instanceof BaseException base) {
            return Response.status(base.getCode())
                    .entity(ApiResponse.error(base.getCode(), base.getMessage()))
                    .build();
        }

        // Bean validation
        if (e instanceof jakarta.validation.ConstraintViolationException cve) {
            String msg = cve.getConstraintViolations()
                    .stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .findFirst()
                    .orElse("Invalid data");

            return Response.status(400)
                    .entity(ApiResponse.error(400, msg))
                    .build();
        }

        // Fallback
        return Response.status(500)
                .entity(ApiResponse.error(500, "Internal error: " + e.getMessage()))
                .build();
    }
}
