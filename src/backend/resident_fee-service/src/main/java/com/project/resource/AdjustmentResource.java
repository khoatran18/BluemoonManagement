package com.project.resource;

import com.project.dto.AdjustmentDTO;
import com.project.entity.Adjustment;
import com.project.exception.ApiResponse;
import com.project.service.AdjustmentService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

@Path("/api/v1/adjustments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdjustmentResource {

    @Inject
    AdjustmentService adjustmentService;

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Retrieve a paginated list of Adjustments based on optional filter.
     * Supports filtering by type, category, name, amount, date range, and status.
     */
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

        // Call service function
        AdjustmentDTO.GetAdjustmentsResponseDTO resDTO = adjustmentService.getAdjustmentsByFilter(
                feeId,
                adjustmentAmount,
                adjustmentType,
                effectiveDate,
                expiryDate,
                page,
                limit
        );

        // Response
        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /**
     * Retrieve a Adjustment record by AdjustmentId
     */
    @GET
    @Path("/{adjustment_id}")
    public Response getAdjustmentById(@PathParam("adjustment_id") Long adjustmentId) {

        // Call service function
        AdjustmentDTO.GetAdjustmentResponseDTO resDTO = adjustmentService.getAdjustmentById(adjustmentId);

        // Response
        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Create a new Adjustment record
     */
    @POST
    public Response createAdjustment(@Valid AdjustmentDTO.PostAdjustmentRequestDTO dto) {

        // Call service function
        adjustmentService.createAdjustment(dto);

        // Response
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Update an existed Adjustment record by AdjustmentId
     */
    @PUT
    @Path("/{adjustment_id}")
    public Response updateAdjustmentById(

            // Call service function
            @PathParam("adjustment_id") Long adjustmentId,

            // Response
            @Valid AdjustmentDTO.PutAdjustmentRequestDTO dto
    ) {

        // Call service function
        adjustmentService.updateAdjustmentById(dto);

        // Response
        return Response.ok(ApiResponse.ok(dto))
                .build();
    }

    ///////////////////////////// For Delete method /////////////////////////////

    /**
     * Delete an existed Adjustment record by AdjustmentId
     */
    @DELETE
    @Path("/{adjustment_id}")
    public Response deleteAdjustmentById(@PathParam("adjustment_id") Long adjustmentId) {

        // Call service function
        adjustmentService.deleteAdjustmentById(adjustmentId);

        // Response
        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
