package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.ResidentDTO.*;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.service.ResidentService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/residents")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ResidentResource {

    @Inject
    ResidentService residentService;

    ////////////////////////////////////////
    // GET LIST
    ////////////////////////////////////////

    @GET
    public Response getResidentsByFilter(
            @QueryParam("apartment_id") Long apartmentId,
            @QueryParam("full_name") String fullName,
            @QueryParam("phone_number") String phoneNumber,
            @QueryParam("email") String email,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        ResidentListResponseDTO resDTO = residentService.getResidentsByFilter(
                apartmentId,
                fullName,
                phoneNumber,
                email,
                page,
                limit
        );

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
        ResidentDetailsDTO resDTO = residentService.getResidentById(residentId);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    ////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////

    @POST
    public Response createResident(
            @Valid ResidentCreateDTO dto
    ) {
        residentService.createResident(dto);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    ////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////

    @PUT
    @Path("/{resident_id}")
    public Response updateResident(
            @PathParam("resident_id") Long residentId,
            @Valid ResidentUpdateDTO dto
    ) {
        residentService.updateResident(residentId, dto);

        return Response.ok(ApiResponse.ok(dto)).build();
    }

    ////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////

    @DELETE
    @Path("/{resident_id}")
    public Response deleteResident(
            @PathParam("resident_id") Long residentId
    ) {
        residentService.deleteResident(residentId);

        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
