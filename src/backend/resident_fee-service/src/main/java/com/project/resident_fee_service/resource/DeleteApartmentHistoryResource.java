package com.project.resident_fee_service.resource;

import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.dto.DeleteApartmentHistoryDTO;
import com.project.resident_fee_service.service.DeleteApartmentHistoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/delete-apartment-histories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeleteApartmentHistoryResource {

    private static final Logger log =
            LoggerFactory.getLogger(DeleteApartmentHistoryResource.class);

    @Inject
    DeleteApartmentHistoryService service;

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    @GET
    public Response getDeleteApartmentHistoriesByFilter(
            @QueryParam("apartment_id") Long apartmentId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[DeleteApartmentHistory] [Resource] getDeleteApartmentHistoriesByFilter Start");
        log.info(
                "Input: apartment_id={}, page={}, limit={}",
                apartmentId, page, limit
        );

        DeleteApartmentHistoryDTO.GetDeleteApartmentHistoriesResponseDTO resDTO =
                service.getDeleteApartmentHistoriesByFilter(
                        apartmentId, page, limit
                );

        log.info("[DeleteApartmentHistory] [Resource] getDeleteApartmentHistoriesByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{history_id}")
    public Response getDeleteApartmentHistoryById(
            @PathParam("history_id") Long historyId
    ) {

        log.info("[DeleteApartmentHistory] [Resource] getDeleteApartmentHistoryById Start");
        log.info("Input: history_id={}", historyId);

        DeleteApartmentHistoryDTO.GetDeleteApartmentHistoryResponseDTO resDTO =
                service.getDeleteApartmentHistoryById(historyId);

        log.info("[DeleteApartmentHistory] [Resource] getDeleteApartmentHistoryById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }
}
