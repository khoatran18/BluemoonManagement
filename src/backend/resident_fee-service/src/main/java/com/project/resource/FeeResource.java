package com.project.resource;

import com.project.dto.FeeDTO;
import com.project.exception.ApiResponse;
import com.project.service.FeeService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

@Path("/api/v1/fees")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeeResource {

    @Inject
    FeeService feeService;

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Retrieve a paginated list of Fees based on optional filter.
     * Supports filtering by type, category, name, amount, date range, and status.
     */
    @GET
    public Response getFeesByFilter(
            @QueryParam("fee_type_id") Long feeTypeId,
            @QueryParam("fee_category_id") Long feeCategoryId,
            @QueryParam("fee_name") String feeName,
            @QueryParam("fee_amount")BigDecimal feeAmount,
            @QueryParam("applicable_month") String applicableMonth,
            @QueryParam("effective_date") String effectiveDate,
            @QueryParam("expiry_date") String expiryDate,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        // Call service function
        FeeDTO.GetFeesResponseDTO resDTO = feeService.getFeesByFilter(
                feeTypeId,
                feeCategoryId,
                feeName,
                feeAmount,
                applicableMonth,
                effectiveDate,
                expiryDate,
                status,
                page,
                limit
        );

        // Response
        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /**
     * Retrieve a Fee record by FeeId
     */
    @GET
    @Path("/{fee_id}")
    public Response getFeeById(@PathParam("fee_id") Long feeId) {

        // Call service function
        FeeDTO.GetFeeResponseDTO resDTO = feeService.getFeeById(feeId);

        // Response
        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Create a new Fee record
     */
    @POST
    public Response createFee(@Valid FeeDTO.PostFeeRequestDTO dto) {

        // Call service function
        feeService.createFee(dto);

        // Response
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Update an existed Fee record by FeeId
     */
    @PUT
    @Path("/{fee_id}")
    public Response updateFeeById(

            // Call service function
            @PathParam("fee_id") Long feeId,

            // Response
            @Valid FeeDTO.PutFeeRequestDTO dto
    ) {

        // Call service function
        feeService.updateFeeById(dto);

        // Response
        return Response.ok(ApiResponse.ok(dto))
                .build();
    }

    ///////////////////////////// For Delete method /////////////////////////////

    /**
     * Delete an existed Fee record by FeeId
     */
    @DELETE
    @Path("/{fee_id}")
    public Response deleteFeeById(@PathParam("fee_id") Long feeId) {

        // Call service function
        feeService.deleteFeeById(feeId);

        // Response
        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
