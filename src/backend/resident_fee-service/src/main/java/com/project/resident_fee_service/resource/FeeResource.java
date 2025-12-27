package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.FeeDTO;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.service.FeeService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Path("/api/v1/fees")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeeResource {

    private static final Logger log =
            LoggerFactory.getLogger(FeeResource.class);

    @Inject
    FeeService feeService;

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    @GET
    public Response getFeesByFilter(
            @QueryParam("fee_type_id") Long feeTypeId,
            @QueryParam("fee_category_id") Long feeCategoryId,
            @QueryParam("fee_name") String feeName,
            @QueryParam("fee_amount") BigDecimal feeAmount,
            @QueryParam("applicable_month") String applicableMonth,
            @QueryParam("effective_date") String effectiveDate,
            @QueryParam("expiry_date") String expiryDate,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[Fee] [Resource] getFeesByFilter Start");
        log.info(
                "Input: feeTypeId={}, feeCategoryId={}, feeName={}, feeAmount={}, applicableMonth={}, effectiveDate={}, expiryDate={}, status={}, page={}, limit={}",
                feeTypeId, feeCategoryId, feeName, feeAmount,
                applicableMonth, effectiveDate, expiryDate, status, page, limit
        );

        FeeDTO.GetFeesResponseDTO resDTO =
                feeService.getFeesByFilter(
                        feeTypeId, feeCategoryId, feeName, feeAmount,
                        applicableMonth, effectiveDate, expiryDate,
                        status, page, limit
                );

        log.info("[Fee] [Resource] getFeesByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{fee_id}")
    public Response getFeeById(
            @PathParam("fee_id") Long feeId
    ) {

        log.info("[Fee] [Resource] getFeeById Start");
        log.info("Input: feeId={}", feeId);

        FeeDTO.GetFeeResponseDTO resDTO =
                feeService.getFeeById(feeId);

        log.info("[Fee] [Resource] getFeeById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // CREATE
    /////////////////////////////

    @POST
    public Response createFee(
            @Valid FeeDTO.PostFeeRequestDTO dto
    ) {

        log.info("[Fee] [Resource] createFee Start");
        log.info("Input: {}", dto);

        feeService.createFee(dto);

        log.info("[Fee] [Resource] createFee End");
        log.info("Output: None");

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    /////////////////////////////
    // UPDATE
    /////////////////////////////

    @PUT
    @Path("/{fee_id}")
    public Response updateFeeById(
            @PathParam("fee_id") Long feeId,
            @Valid FeeDTO.PutFeeRequestDTO dto
    ) {

        log.info("[Fee] [Resource] updateFeeById Start");
        log.info("Input: feeId={}, dto={}", feeId, dto);

        feeService.updateFeeById(dto);

        log.info("[Fee] [Resource] updateFeeById End");
        log.info("Output: {}", dto);

        return Response.ok(ApiResponse.ok(dto)).build();
    }

    /////////////////////////////
    // DELETE
    /////////////////////////////

    @DELETE
    @Path("/{fee_id}")
    public Response deleteFeeById(
            @PathParam("fee_id") Long feeId
    ) {

        log.info("[Fee] [Resource] deleteFeeById Start");
        log.info("Input: feeId={}", feeId);

        feeService.deleteFeeById(feeId);

        log.info("[Fee] [Resource] deleteFeeById End");
        log.info("Output: None");

        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
