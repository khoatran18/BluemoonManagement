package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.ResidentDTO.*;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.entity.Account;
import com.project.resident_fee_service.middleware.RoleAllowedEx;
import com.project.resident_fee_service.service.ResidentService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/residents")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ResidentResource {

    private static final Logger log =
            LoggerFactory.getLogger(ResidentResource.class);

    @Inject
    ResidentService residentService;

    ////////////////////////////////////////
    // GET LIST
    ////////////////////////////////////////

    @GET
    @RoleAllowedEx({Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector})
    public Response getResidentsByFilter(
            @QueryParam("apartment_id") Long apartmentId,
            @QueryParam("full_name") String fullName,
            @QueryParam("phone_number") String phoneNumber,
            @QueryParam("email") String email,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[Resident] [Resource] getResidentsByFilter Start");
        log.info("Input: apartmentId={}, fullName={}, phoneNumber={}, email={}, page={}, limit={}",
                apartmentId, fullName, phoneNumber, email, page, limit);

        ResidentListResponseDTO resDTO =
                residentService.getResidentsByFilter(
                        apartmentId, fullName, phoneNumber, email, page, limit
                );

        log.info("[Resident] [Resource] getResidentsByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    ////////////////////////////////////////
    // GET DETAIL
    ////////////////////////////////////////

    @GET
    @Path("/{resident_id}")
    public Response getResidentById(
            @PathParam("resident_id") Long residentId
    ) {

        log.info("[Resident] [Resource] getResidentById Start");
        log.info("Input: residentId={}", residentId);

        ResidentDetailsDTO resDTO =
                residentService.getResidentById(residentId);

        log.info("[Resident] [Resource] getResidentById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    ////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////

    @POST
    @RoleAllowedEx({Account.RoleEnum.Admin})
    public Response createResident(
            @Valid ResidentCreateDTO dto
    ) {

        log.info("[Resident] [Resource] createResident Start");
        log.info("Input: {}", dto);

        residentService.createResident(dto);

        log.info("[Resident] [Resource] createResident End");
        log.info("Output: None");

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    ////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////

    @PUT
    @Path("/{resident_id}")
    @RoleAllowedEx({Account.RoleEnum.Admin})
    public Response updateResident(
            @PathParam("resident_id") Long residentId,
            @Valid ResidentUpdateDTO dto
    ) {

        log.info("[Resident] [Resource] updateResident Start");
        log.info("Input: residentId={}, dto={}", residentId, dto);

        residentService.updateResident(residentId, dto);

        log.info("[Resident] [Resource] updateResident End");
        log.info("Output: {}", dto);

        return Response.ok(ApiResponse.ok(dto)).build();
    }

    ////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////

    @DELETE
    @Path("/{resident_id}")
    @RoleAllowedEx({Account.RoleEnum.Admin})
    public Response deleteResident(
            @PathParam("resident_id") Long residentId
    ) {

        log.info("[Resident] [Resource] deleteResident Start");
        log.info("Input: residentId={}", residentId);

        residentService.deleteResident(residentId);

        log.info("[Resident] [Resource] deleteResident End");
        log.info("Output: None");

        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
