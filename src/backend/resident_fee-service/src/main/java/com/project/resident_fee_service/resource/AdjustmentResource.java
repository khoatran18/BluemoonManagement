package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.AdjustmentDTO;
import com.project.resident_fee_service.entity.Account;
import com.project.resident_fee_service.entity.Adjustment;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.middleware.RoleAllowedEx;
import com.project.resident_fee_service.service.AdjustmentService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Path("/api/v1/adjustments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdjustmentResource {

    private static final Logger log =
            LoggerFactory.getLogger(AdjustmentResource.class);

    @Inject
    AdjustmentService adjustmentService;

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    @GET
    public Response getAdjustmentsByFilter(
            @QueryParam("fee_id") Long feeId,
            @QueryParam("adjustment_amount") BigDecimal adjustmentAmount,
            @QueryParam("adjustment_type") Adjustment.AdjustmentType adjustmentType,
            @QueryParam("effective_date") String effectiveDate,
            @QueryParam("expiry_date") String expiryDate,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[Fee] [Resource] getAdjustmentsByFilter Start");
        log.info(
                "Input: feeId={}, adjustmentAmount={}, adjustmentType={}, effectiveDate={}, expiryDate={}, page={}, limit={}",
                feeId, adjustmentAmount, adjustmentType,
                effectiveDate, expiryDate, page, limit
        );

        AdjustmentDTO.GetAdjustmentsResponseDTO resDTO =
                adjustmentService.getAdjustmentsByFilter(
                        feeId,
                        adjustmentAmount,
                        adjustmentType,
                        effectiveDate,
                        expiryDate,
                        page,
                        limit
                );

        log.info("[Fee] [Resource] getAdjustmentsByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{adjustment_id}")
    public Response getAdjustmentById(
            @PathParam("adjustment_id") Long adjustmentId
    ) {

        log.info("[Fee] [Resource] getAdjustmentById Start");
        log.info("Input: adjustmentId={}", adjustmentId);

        AdjustmentDTO.GetAdjustmentResponseDTO resDTO =
                adjustmentService.getAdjustmentById(adjustmentId);

        log.info("[Fee] [Resource] getAdjustmentById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    @GET
    @Path("/apartment_specific_adjustments")
    public Response getApartmentSpecificAdjustments(
            @QueryParam("adjustment_amount") BigDecimal adjustmentAmount,
            @QueryParam("adjustment_type") Adjustment.AdjustmentType adjustmentType,
            @QueryParam("effective_date") String effectiveDate,
            @QueryParam("expiry_date") String expiryDate,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[Fee] [Resource] getApartmentSpecificAdjustments Start");
        log.info(
                "Input: adjustmentAmount={}, adjustmentType={}, effectiveDate={}, expiryDate={}, page={}, limit={}",
                adjustmentAmount, adjustmentType,
                effectiveDate, expiryDate, page, limit
        );

        AdjustmentDTO.GetAdjustmentsResponseDTO resDTO =
                adjustmentService.getApartmentSpecificAdjustmentsByFilter(
                        adjustmentAmount,
                        adjustmentType,
                        effectiveDate,
                        expiryDate,
                        page,
                        limit
                );

        log.info("[Fee] [Resource] getApartmentSpecificAdjustments End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // CREATE
    /////////////////////////////

    @POST
    @RoleAllowedEx({Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector})
    public Response createAdjustment(
            @Valid AdjustmentDTO.PostAdjustmentRequestDTO dto
    ) {

        log.info("[Fee] [Resource] createAdjustment Start");
        log.info("Input: {}", dto);

        adjustmentService.createAdjustment(dto);

        log.info("[Fee] [Resource] createAdjustment End");
        log.info("Output: None");

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    /////////////////////////////
    // UPDATE
    /////////////////////////////

    @PUT
    @Path("/{adjustment_id}")
    @RoleAllowedEx({Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector})
    public Response updateAdjustmentById(
            @PathParam("adjustment_id") Long adjustmentId,
            @Valid AdjustmentDTO.PutAdjustmentRequestDTO dto
    ) {

        log.info("[Fee] [Resource] updateAdjustmentById Start");
        log.info("Input: adjustmentId={}, dto={}", adjustmentId, dto);

        adjustmentService.updateAdjustmentById(dto);

        log.info("[Fee] [Resource] updateAdjustmentById End");
        log.info("Output: {}", dto);

        return Response.ok(ApiResponse.ok(dto)).build();
    }

    /////////////////////////////
    // DELETE
    /////////////////////////////

    @DELETE
    @Path("/{adjustment_id}")
    @RoleAllowedEx({Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector})
    public Response deleteAdjustmentById(
            @PathParam("adjustment_id") Long adjustmentId
    ) {

        log.info("[Fee] [Resource] deleteAdjustmentById Start");
        log.info("Input: adjustmentId={}", adjustmentId);

        adjustmentService.deleteAdjustmentById(adjustmentId);

        log.info("[Fee] [Resource] deleteAdjustmentById End");
        log.info("Output: None");

        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
