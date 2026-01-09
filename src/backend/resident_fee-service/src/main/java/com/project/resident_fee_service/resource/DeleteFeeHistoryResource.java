package com.project.resident_fee_service.resource;

import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.dto.DeleteFeeHistoryDTO;
import com.project.resident_fee_service.service.DeleteFeeHistoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/delete-fee-histories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeleteFeeHistoryResource {

    private static final Logger log =
            LoggerFactory.getLogger(DeleteFeeHistoryResource.class);

    @Inject
    DeleteFeeHistoryService service;

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    @GET
    public Response getDeleteFeeHistoriesByFilter(
            @QueryParam("fee_id") Long feeId,
            @QueryParam("fee_type_id") Long feeTypeId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[DeleteFeeHistory] [Resource] getDeleteFeeHistoriesByFilter Start");
        log.info(
                "Input: fee_id={}, fee_type_id={}, page={}, limit={}",
                feeId, feeTypeId, page, limit
        );

        DeleteFeeHistoryDTO.GetDeleteFeeHistoriesResponseDTO resDTO =
                service.getDeleteFeeHistoriesByFilter(
                        feeId, feeTypeId, page, limit
                );

        log.info("[DeleteFeeHistory] [Resource] getDeleteFeeHistoriesByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{history_id}")
    public Response getDeleteFeeHistoryById(
            @PathParam("history_id") Long historyId
    ) {

        log.info("[DeleteFeeHistory] [Resource] getDeleteFeeHistoryById Start");
        log.info("Input: history_id={}", historyId);

        DeleteFeeHistoryDTO.GetDeleteFeeHistoryResponseDTO resDTO =
                service.getDeleteFeeHistoryById(historyId);

        log.info("[DeleteFeeHistory] [Resource] getDeleteFeeHistoryById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }
}
