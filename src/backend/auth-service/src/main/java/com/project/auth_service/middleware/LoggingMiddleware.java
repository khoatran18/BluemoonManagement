package com.project.auth_service.middleware;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class  LoggingMiddleware implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext req) {
        long start = System.currentTimeMillis();
        req.setProperty("startTime", start);

        System.out.println("[IN ] " + req.getMethod() + " " + req.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) {
        long start = (long) req.getProperty("startTime");
        long duration = System.currentTimeMillis() - start;

        System.out.println("[OUT] status=" + res.getStatus() +
                " time=" + duration + "ms");
    }
}
