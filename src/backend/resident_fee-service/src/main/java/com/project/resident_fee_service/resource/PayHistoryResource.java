package com.project.resident_fee_service.resource;

import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.dto.PayHistoryDTO;
import com.project.resident_fee_service.service.PayHistoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/v1/pay-histories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PayHistoryResource {

    private static final Logger log =
            LoggerFactory.getLogger(PayHistoryResource.class);

    @Inject
    PayHistoryService payHistoryService;

    @GET
    public Response getPayHistoriesByFilter(
            @QueryParam("apartment_id") Long apartmentID,
            @QueryParam("fee_id") Long feeID,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[PayHistory] [Resource] getFeesByFilter Start");
        log.info(
                "Input: apartment_id={}, fee_id={}, page={}, limit={}",
                apartmentID, feeID, page, limit
        );

        PayHistoryDTO.GetPayHistoriesResponseDTO resDTO =
                payHistoryService.getPayHistoriesByFilter(
                        apartmentID, feeID, page, limit
                );

        log.info("[PayHistory] [Resource] getPayHistoriesByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{pay_history_id}")
    public Response getPayHistoryById(
            @PathParam("pay_history_id") Long payHistoryID
    ) {

        log.info("[PayHistory] [Resource] getPayHistoryById Start");
        log.info("Input: pay_history_id={}", payHistoryID);

        PayHistoryDTO.GetPayHistoryResponseDTO resDTO =
                payHistoryService.getPayHistoryById(payHistoryID);

        log.info("[PayHistory] [Resource] getPayHistoryById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }


}
