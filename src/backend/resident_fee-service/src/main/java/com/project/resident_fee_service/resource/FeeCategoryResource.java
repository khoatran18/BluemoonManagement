package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.FeeCategoryDTO;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.service.FeeCategoryService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/fee-categories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeeCategoryResource {

    @Inject
    FeeCategoryService feeCategoryService;

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Retrieve a paginated list of FeeCategories based on optional filter.
     * Supports filtering by type.
     */
    @GET
    public Response getFeeCategoriesByFilter(
            @QueryParam("fee_type_id") Long feeTypeId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        // Call service function
        FeeCategoryDTO.GetFeeCategoriesResponseDTO resDTO = feeCategoryService.getFeeCategoriesByFilter(
                feeTypeId,
                page,
                limit
        );

        // Response
        return Response.ok(ApiResponse.ok(resDTO))
                .build();
    }

    /**
     * Retrieve a FeeCategory record by FeeId
     */
    @GET
    @Path("/{fee_category_id}")
    public Response getFeeCategoryById(
            @PathParam("fee_category_id") Long feeCategoryId
    ) {
        // Call service function
        FeeCategoryDTO.GetFeeCategoryResponseDTO resDTO = feeCategoryService.getFeeCategoryById(feeCategoryId);

        // Response
        return Response.ok(ApiResponse.ok(resDTO))
                .build();
    }

    ///////////////////////////// For Post method /////////////////////////////

    /**
     * Create a new FeeCategory record
     */
    @POST
    public Response createFeeCategory(@Valid FeeCategoryDTO.PostFeeCategoryRequestDTO dto) {

        // Call service function
        feeCategoryService.createFeeCategory(dto);

        // Response
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    ///////////////////////////// For Put method /////////////////////////////

    /**
     * Update an existed FeeCategory record by FeeId
     */
    @PUT
    @Path("/{fee_category_id}")
    public Response updateFeeCategoryById(
            @PathParam("fee_category_id") Long feeCategoryId,
            @Valid FeeCategoryDTO.PutFeeCategoryRequestDTO dto
    ) {

        // Call service function
        feeCategoryService.updateFeeCategoryById(dto);

        // Response
        return Response.ok(ApiResponse.ok(dto))
                .build();
    }

    ///////////////////////////// For Delete method /////////////////////////////

    /**
     * Delete an existed FeeCategory record by FeeId
     */
    @DELETE
    @Path("/{fee_category_id}")
    public Response deleteFeeCategoryById(@PathParam("fee_category_id") Long feeCategoryId) {

        // Call service function
        feeCategoryService.deleteFeeCategoryById(feeCategoryId);

        // Response
        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }

}
