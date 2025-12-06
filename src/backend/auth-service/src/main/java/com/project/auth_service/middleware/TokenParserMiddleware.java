package com.project.auth_service.middleware;

import com.project.auth_service.entity.Account;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;

@Provider
@Priority(2)
public class TokenParserMiddleware implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext ctx) {

        // Không có token thì để Permission middleware xử lý
        if (jwt == null || jwt.getRawToken().isEmpty()) {
            return;
        }

        try {
            String accountId = jwt.getClaim("accountId");
            String roleStr = jwt.getClaim("role");

            Account.RoleEnum role = Account.RoleEnum.valueOf(roleStr);

            JwtPayload payload = new JwtPayload(accountId, role);

            ctx.setProperty("user", payload);

        } catch (Exception e) {
            ctx.abortWith(
                    jakarta.ws.rs.core.Response.status(401)
                            .entity("Invalid token")
                            .build()
            );
        }
    }
}
