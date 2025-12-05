package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.service.ApartmentService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/apartments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApartmentResource {

    @Inject
    ApartmentService apartmentService;

    ////////////////////////////////////////
    // GET LIST
    ////////////////////////////////////////

    @GET
    public Response getApartmentsByFilter(
            @QueryParam("building") String building,
            @QueryParam("room_number") String roomNumber,
            @QueryParam("head_resident_id") Long headResidentId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        ApartmentListResponseDTO resDTO = apartmentService.getApartmentsByFilter(
                building,
                roomNumber,
                headResidentId,
                page,
                limit
        );

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    ////////////////////////////////////////
    // GET DETAIL
    ////////////////////////////////////////

    @GET
    @Path("/{apartment_id}")
    public Response getApartmentById(
            @PathParam("apartment_id") Long apartmentId
    ) {
        ApartmentDetailsDTO resDTO = apartmentService.getApartmentById(apartmentId);
        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    @GET
    @Path("/apartment_specific_adjustments/{apartment_id}")
    public Response getApartmentSpecificAdjustments(
            @PathParam("apartment_id") Long apartmentId
    ) {
        ApartmentSpecificAdjustmentsResponseDTO resDTO = apartmentService.getApartmentSpecificAdjustments(apartmentId);
        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    ////////////////////////////////////////
    // CREATE
    ////////////////////////////////////////

    @POST
    public Response createApartment(
            @Valid ApartmentCreateDTO dto
    ) {
        apartmentService.createApartment(dto);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    ////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////

    @PUT
    @Path("/{apartment_id}")
    public Response updateApartment(
            @PathParam("apartment_id") Long apartmentId,
            @Valid ApartmentUpdateDTO dto
    ) {
        apartmentService.updateApartment(apartmentId, dto);

        return Response.ok(ApiResponse.ok(dto)).build();
    }

    @PUT
    @Path("/apartment_specific_adjustments/{apartment_id}")
    public Response updateApartmentSpecificAdjustments(
            @PathParam("apartment_id") Long apartmentID,
            @Valid ApartmentSpecificAdjustmentsRequestDTO dto
    ) {
        apartmentService.updateSpecificAdjustments(apartmentID, dto);
        return Response.ok(ApiResponse.ok(dto)).build();
    }

    ////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////

    @DELETE
    @Path("/{apartment_id}")
    public Response deleteApartment(
            @PathParam("apartment_id") Long apartmentId
    ) {
        apartmentService.deleteApartment(apartmentId);

        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
