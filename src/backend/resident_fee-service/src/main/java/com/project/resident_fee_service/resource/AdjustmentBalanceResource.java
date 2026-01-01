package com.project.resident_fee_service.resource;

import com.project.common_package.exception.ApiResponse;
import com.project.common_package.exception.InternalServerException;
import com.project.common_package.exception.NotFoundException;
import com.project.resident_fee_service.dto.AdjustmentBalanceDTO;
import com.project.resident_fee_service.entity.AdjustmentBalance;
import com.project.resident_fee_service.mapper.LocalDateMapper;
import com.project.resident_fee_service.mapper.AdjustmentBalanceMapper;
import com.project.resident_fee_service.repository.AdjustmentBalanceRepository;
import com.project.resident_fee_service.service.AdjustmentBalanceService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/v1/adjustment-balances")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdjustmentBalanceResource {

    private static final Logger log =
            LoggerFactory.getLogger(AdjustmentBalanceResource.class);

    @Inject
    AdjustmentBalanceService adjustmentBalanceService;

    @GET
    public Response getAdjustmentBalancesByFilter(
            @QueryParam("apartment_id") Long apartmentID,
            @QueryParam("fee_id") Long feeID,
            @QueryParam("adjustment_id") Long adjustmentID,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[AdjustmentBalance] [Resource] getFeesByFilter Start");
        log.info(
                "Input: apartment_id={}, fee_id={}, page={}, limit={}",
                apartmentID, feeID, page, limit
        );

        AdjustmentBalanceDTO.GetAdjustmentBalancesResponseDTO resDTO =
                adjustmentBalanceService.getAdjustmentBalancesByFilter(
                        apartmentID, feeID, adjustmentID, page, limit
                );

        log.info("[AdjustmentBalance] [Resource] getAdjustmentBalancesByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{adjustment_balance_id}")
    public Response getAdjustmentBalanceById(
            @PathParam("adjustment_balance_id") Long adjustmentBalanceID
    ) {

        log.info("[AdjustmentBalance] [Resource] getAdjustmentBalanceById Start");
        log.info("Input: adjustment_balance_id={}", adjustmentBalanceID);

        AdjustmentBalanceDTO.GetAdjustmentBalanceResponseDTO resDTO =
                adjustmentBalanceService.getAdjustmentBalanceById(adjustmentBalanceID);

        log.info("[AdjustmentBalance] [Resource] getAdjustmentBalanceById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }


}
