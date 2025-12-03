package com.project.resource;

import com.project.resident_fee_service.dto.ApartmentFeeStatusDTO;
import com.project.resident_fee_service.exception.ApiResponse;
import com.project.resident_fee_service.service.ApartmentFeeStatusService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/apartment-fee-statuses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApartmentFeeStatusResource {

    @Inject
    ApartmentFeeStatusService service;


    // ==========================================================
    // =============== 5.1 GET Method ===========================
    // ==========================================================

    /**
     * GET: Return the apartment fee status for the given apartment ID.
     * API: /api/v1/apartment-fee-statuses/{apartment_id}
     */
    @GET
    @Path("/{apartment_id}")
    public Response getStatusByApartmentId(
            @PathParam("apartment_id") Long apartmentId
    ) {

        ApartmentFeeStatusDTO.FeeStatusDTO resDTO =
                service.getStatusByApartmentId(apartmentId);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }


    // ==========================================================
    // =============== 5.2 PUT Method ===========================
    // ==========================================================

    /**
     * PUT: Update the apartment fee status by apartment ID.
     * API: /api/v1/apartment-fee-statuses/{apartment_id}
     */
    @PUT
    @Path("/{apartment_id}")
    public Response updateStatusByApartmentId(
            @PathParam("apartment_id") Long apartmentId,
            @Valid ApartmentFeeStatusDTO.FeeStatusUpdateDTO dto
    ) {

        service.updateStatusByApartmentId(apartmentId, dto);

        return Response.ok(ApiResponse.ok("Updated successfully")).build();
    }
}
