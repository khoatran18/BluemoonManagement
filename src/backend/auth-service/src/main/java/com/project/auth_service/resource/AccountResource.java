package com.project.auth_service.resource;

import com.project.auth_service.dto.AccountDTO;
import com.project.auth_service.service.AccountService;
import com.project.common_package.exception.ApiResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/auth_service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    AccountService accountService;

    ////////////////////////////////////////
    // 1. LOGIN
    ////////////////////////////////////////
    @POST
    @Path("/login")
    public Response login(@Valid AccountDTO.LoginRequestDTO dto) {

        AccountDTO.LoginResponseDTO resDTO = accountService.login(dto);

        return Response.ok(ApiResponse.ok(resDTO))
                .build();
    }

    ////////////////////////////////////////
    // 2. REGISTER
    ////////////////////////////////////////
    @POST
    @Path("/register")
    public Response register(@Valid AccountDTO.RegisterRequestDTO dto) {

        accountService.register(dto);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    ////////////////////////////////////////
    // 3. CHANGE PASSWORD
    ////////////////////////////////////////
    @POST
    @Path("/change-password")
    public Response changePassword(@Valid AccountDTO.ChangePasswordRequestDTO dto) {

        accountService.changePassword(dto);

        return Response.ok(ApiResponse.ok("Password updated successfully"))
                .build();
    }

    ////////////////////////////////////////
    // 4. REFRESH TOKEN
    ////////////////////////////////////////
    @POST
    @Path("/refresh")
    public Response refresh(@Valid AccountDTO.RefreshTokenRequestDTO dto) {

        AccountDTO.RefreshTokenResponseDTO resDTO = accountService.refresh(dto);

        return Response.ok(ApiResponse.ok(resDTO))
                .build();
    }
}
