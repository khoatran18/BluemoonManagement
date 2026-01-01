package com.project.resident_fee_service.service;

import com.project.resident_fee_service.entity.Account;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class JwtProvider {

    private static final long ACCESS_TOKEN_EXPIRE = 1000L * 60 * 60 * 24 * 200;
    private static final long REFRESH_TOKEN_EXPIRE = 1000L * 60 * 60 * 24 * 500;

    @Inject
    JWTParser jwtParser;

    ///////////////////////////////////////////////
    // 1. Generate Access Token
    ///////////////////////////////////////////////
    public String generateAccessToken(Account account) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", String.valueOf(account.getAccountId()));
        claims.put("role", account.getRole().name());
        claims.put("type", "access");

        return Jwt.claims(claims)
                .issuer("auth-service")
                .subject(account.getUsername())
                .expiresAt(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE)
                .sign();
    }

    ///////////////////////////////////////////////
    // 2. Generate Refresh Token
    ///////////////////////////////////////////////
    public String generateRefreshToken(Account account) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", String.valueOf(account.getAccountId()));
        claims.put("role", account.getRole().name());
        claims.put("type", "refresh");

        return Jwt.claims(claims)
                .issuer("auth-service")
                .subject(account.getUsername())
                .expiresAt(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE)
                .sign();
    }

    ///////////////////////////////////////////////
    // 3. Validate Refresh Token
    ///////////////////////////////////////////////
    public String validateRefreshToken(String token) {
        try {
            var jwt = jwtParser.parse(token);   // <<=== CHUẨN QUARKUS 3

            // check type
            String type = jwt.getClaim("type");
            if (!"refresh".equals(type)) {
                return null;
            }

            // username = subject
            return jwt.getSubject();

        } catch (Exception e) {
            return null;
        }
    }
}
