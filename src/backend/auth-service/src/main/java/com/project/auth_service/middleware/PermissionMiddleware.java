package com.project.auth_service.middleware;

import com.project.auth_service.entity.Account;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Arrays;

@Provider
@Priority(3)
public class PermissionMiddleware implements ContainerRequestFilter {

    @Inject
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {

        JwtPayload user = (JwtPayload) ctx.getProperty("user");

        if (user == null) {
            ctx.abortWith(Response.status(401).entity("Missing or invalid token").build());
            return;
        }

        RoleAllowedEx rolesAllowed = resourceInfo.getResourceMethod().getAnnotation(RoleAllowedEx.class);

        if (rolesAllowed == null) {
            rolesAllowed = resourceInfo.getResourceClass().getAnnotation(RoleAllowedEx.class);
        }

        if (rolesAllowed == null) {
            return;
        }

        Account.RoleEnum[] requeiredRoles = rolesAllowed.value();

        if (!Arrays.asList(requeiredRoles).contains(user.role)) {
            ctx.abortWith(Response.status(403).entity("Forbidden").build());
        }

    }
}
