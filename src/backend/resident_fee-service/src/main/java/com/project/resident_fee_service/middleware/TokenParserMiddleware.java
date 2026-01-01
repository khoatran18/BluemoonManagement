package com.project.resident_fee_service.middleware;

import com.project.resident_fee_service.entity.Account;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Provider
@Priority(2)
public class TokenParserMiddleware implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext ctx) {

        // Không có JWT → bỏ qua (public endpoint)
        if (jwt == null) {
            return;
        }

        String rawToken = jwt.getRawToken();

        if (rawToken == null || rawToken.isBlank()) {
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
