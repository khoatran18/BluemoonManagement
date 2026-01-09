package com.project.resident_fee_service.resource;

import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.dto.DeleteResidentHistoryDTO;
import com.project.resident_fee_service.service.DeleteResidentHistoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/delete-resident-histories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeleteResidentHistoryResource {

    private static final Logger log =
            LoggerFactory.getLogger(DeleteResidentHistoryResource.class);

    @Inject
    DeleteResidentHistoryService service;

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    @GET
    public Response getDeleteResidentHistoriesByFilter(
            @QueryParam("resident_id") Long residentId,
            @QueryParam("apartment_id") Long apartmentId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[DeleteResidentHistory] [Resource] getDeleteResidentHistoriesByFilter Start");
        log.info(
                "Input: resident_id={}, apartment_id={}, page={}, limit={}",
                residentId, apartmentId, page, limit
        );

        DeleteResidentHistoryDTO.GetDeleteResidentHistoriesResponseDTO resDTO =
                service.getDeleteResidentHistoriesByFilter(
                        residentId, apartmentId, page, limit
                );

        log.info("[DeleteResidentHistory] [Resource] getDeleteResidentHistoriesByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{history_id}")
    public Response getDeleteResidentHistoryById(
            @PathParam("history_id") Long historyId
    ) {

        log.info("[DeleteResidentHistory] [Resource] getDeleteResidentHistoryById Start");
        log.info("Input: history_id={}", historyId);

        DeleteResidentHistoryDTO.GetDeleteResidentHistoryResponseDTO resDTO =
                service.getDeleteResidentHistoryById(historyId);

        log.info("[DeleteResidentHistory] [Resource] getDeleteResidentHistoryById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }
}
