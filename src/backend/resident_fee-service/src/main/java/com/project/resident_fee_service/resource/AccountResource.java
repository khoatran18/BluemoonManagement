package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.AccountDTO;
import com.project.resident_fee_service.service.AccountService;
import com.project.common_package.exception.ApiResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/auth_service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private static final Logger log =
            LoggerFactory.getLogger(AccountResource.class);

    @Inject
    AccountService accountService;

    ////////////////////////////////////////
    // 1. LOGIN
    ////////////////////////////////////////
    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid AccountDTO.LoginRequestDTO dto) {

        log.info("[Account] [Resource] login Start");
        log.info("Input: LoginRequestDTO={}", dto);

        AccountDTO.LoginResponseDTO resDTO = accountService.login(dto);

        log.info("[Account] [Resource] login End");
        log.info("Output: LoginResponseDTO " + resDTO);

        return Response.ok(ApiResponse.ok(resDTO))
                .build();
    }

    ////////////////////////////////////////
    // 2. REGISTER
    ////////////////////////////////////////
    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid AccountDTO.RegisterRequestDTO dto) {

        log.info("[Account] [Resource] register Start");
        log.info("Input: RegisterRequestDTO={}", dto);

        accountService.register(dto);

        log.info("[Account] [Resource] register End");
        log.info("Output: None");

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    ////////////////////////////////////////
    // 3. CHANGE PASSWORD
    ////////////////////////////////////////
    @POST
    @Path("/change-password")
    @PermitAll
    public Response changePassword(@Valid AccountDTO.ChangePasswordRequestDTO dto) {

        log.info("[Account] [Resource] changePassword Start");
        log.info("Input: ChangePasswordRequestDTO={}", dto);

        accountService.changePassword(dto);

        log.info("[Account] [Resource] changePassword End");
        log.info("Output: None");

        return Response.ok(ApiResponse.ok("Password updated successfully"))
                .build();
    }

    ////////////////////////////////////////
    // 4. REFRESH TOKEN
    ////////////////////////////////////////
    @POST
    @Path("/refresh")
    @PermitAll
    public Response refresh(@Valid AccountDTO.RefreshTokenRequestDTO dto) {

        log.info("[Account] [Resource] refresh Start");
        log.info("Input: RefreshTokenRequestDTO={}", dto);

        AccountDTO.RefreshTokenResponseDTO resDTO = accountService.refresh(dto);

        log.info("[Account] [Resource] refresh End");
        log.info("Output: RefreshTokenResponseDTO " + resDTO);

        return Response.ok(ApiResponse.ok(resDTO))
                .build();
    }
}
