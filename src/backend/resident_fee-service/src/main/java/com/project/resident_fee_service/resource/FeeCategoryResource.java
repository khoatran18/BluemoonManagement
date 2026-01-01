package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.FeeCategoryDTO;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.entity.Account;
import com.project.resident_fee_service.middleware.RoleAllowedEx;
import com.project.resident_fee_service.service.FeeCategoryService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/fee-categories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeeCategoryResource {

    private static final Logger log =
            LoggerFactory.getLogger(FeeCategoryResource.class);

    @Inject
    FeeCategoryService feeCategoryService;

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    @GET
    public Response getFeeCategoriesByFilter(
            @QueryParam("fee_type_id") Long feeTypeId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {

        log.info("[Fee] [Resource] getFeeCategoriesByFilter Start");
        log.info("Input: feeTypeId={}, page={}, limit={}", feeTypeId, page, limit);

        FeeCategoryDTO.GetFeeCategoriesResponseDTO resDTO =
                feeCategoryService.getFeeCategoriesByFilter(
                        feeTypeId, page, limit
                );

        log.info("[Fee] [Resource] getFeeCategoriesByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{fee_category_id}")
    public Response getFeeCategoryById(
            @PathParam("fee_category_id") Long feeCategoryId
    ) {

        log.info("[Fee] [Resource] getFeeCategoryById Start");
        log.info("Input: feeCategoryId={}", feeCategoryId);

        FeeCategoryDTO.GetFeeCategoryResponseDTO resDTO =
                feeCategoryService.getFeeCategoryById(feeCategoryId);

        log.info("[Fee] [Resource] getFeeCategoryById End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // CREATE
    /////////////////////////////

    @POST
    @RoleAllowedEx({Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector})
    public Response createFeeCategory(
            @Valid FeeCategoryDTO.PostFeeCategoryRequestDTO dto
    ) {

        log.info("[Fee] [Resource] createFeeCategory Start");
        log.info("Input: {}", dto);

        feeCategoryService.createFeeCategory(dto);

        log.info("[Fee] [Resource] createFeeCategory End");
        log.info("Output: None");

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(dto))
                .build();
    }

    /////////////////////////////
    // UPDATE
    /////////////////////////////

    @PUT
    @Path("/{fee_category_id}")
    @RoleAllowedEx({Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector})
    public Response updateFeeCategoryById(
            @PathParam("fee_category_id") Long feeCategoryId,
            @Valid FeeCategoryDTO.PutFeeCategoryRequestDTO dto
    ) {

        log.info("[Fee] [Resource] updateFeeCategoryById Start");
        log.info("Input: feeCategoryId={}, dto={}", feeCategoryId, dto);

        feeCategoryService.updateFeeCategoryById(dto);

        log.info("[Fee] [Resource] updateFeeCategoryById End");
        log.info("Output: {}", dto);

        return Response.ok(ApiResponse.ok(dto)).build();
    }

    /////////////////////////////
    // DELETE
    /////////////////////////////

    @DELETE
    @Path("/{fee_category_id}")
    @RoleAllowedEx({Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector})
    public Response deleteFeeCategoryById(
            @PathParam("fee_category_id") Long feeCategoryId
    ) {

        log.info("[Fee] [Resource] deleteFeeCategoryById Start");
        log.info("Input: feeCategoryId={}", feeCategoryId);

        feeCategoryService.deleteFeeCategoryById(feeCategoryId);

        log.info("[Fee] [Resource] deleteFeeCategoryById End");
        log.info("Output: None");

        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
