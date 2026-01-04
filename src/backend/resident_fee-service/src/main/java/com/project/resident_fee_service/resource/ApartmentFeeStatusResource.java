package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.ApartmentFeeStatusDTO;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.entity.Account;
import com.project.resident_fee_service.middleware.RoleAllowedEx;
import com.project.resident_fee_service.service.ApartmentFeeStatusService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/apartment-fee-statuses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApartmentFeeStatusResource {

    private static final Logger log =
            LoggerFactory.getLogger(ApartmentFeeStatusResource.class);

    @Inject
    ApartmentFeeStatusService service;

    // ==========================================================
    // GET
    // ==========================================================

    @GET
    @Path("/{apartment_id}")
    public Response getStatusByApartmentId(
            @PathParam("apartment_id") Long apartmentId
    ) {

        log.info("[Fee] [Resource] getStatusByApartmentId Start");
        log.info("Input: apartmentId={}", apartmentId);

        ApartmentFeeStatusDTO.FeeStatusDTO resDTO =
                service.getStatusByApartmentId(apartmentId);

        log.info("[Fee] [Resource] getStatusByApartmentId End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    // ==========================================================
    // PUT
    // ==========================================================

    @PUT
    @Path("/{apartment_id}")
    @RoleAllowedEx({Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector})
    public Response updateStatusByApartmentId(
            @PathParam("apartment_id") Long apartmentId,
            @Valid ApartmentFeeStatusDTO.FeeStatusUpdateDTO dto
    ) {

        log.info("[Fee] [Resource] updateStatusByApartmentId Start");
        log.info("Input: apartmentId={}, dto={}", apartmentId, dto);

        service.updateStatusByApartmentId(apartmentId, dto);

        log.info("[Fee] [Resource] updateStatusByApartmentId End");
        log.info("Output: None");

        return Response.ok(ApiResponse.ok("Updated successfully")).build();
    }
}
