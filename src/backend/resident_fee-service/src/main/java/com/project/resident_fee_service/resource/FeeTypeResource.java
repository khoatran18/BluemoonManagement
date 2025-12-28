package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.FeeTypeDTO;
import com.project.resident_fee_service.service.FeeTypeService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("api/v1/fee-types")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeeTypeResource {

    private static final Logger log =
            LoggerFactory.getLogger(FeeTypeResource.class);

    @Inject
    FeeTypeService feeTypeService;

    /////////////////////////////
    // GET LIST
    /////////////////////////////

    @GET
    public Response getFeeTypesByFilter() {

        log.info("[Fee] [Resource] getFeeTypesByFilter Start");
        log.info("Input: None");

        FeeTypeDTO.GetFeeTypesResponseDTO resDTO =
                feeTypeService.getFeeTypesByFilter();

        log.info("[Fee] [Resource] getFeeTypesByFilter End");
        log.info("Output: {}", resDTO);

        return Response.ok(resDTO).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/{fee_type_id}")
    public Response getFeeTypeById(
            @PathParam("fee_type_id") Long feeTypeId
    ) {

        log.info("[Fee] [Resource] getFeeTypeById Start");
        log.info("Input: feeTypeId={}", feeTypeId);

        FeeTypeDTO.GetFeeTypeResponseDTO resDTO =
                feeTypeService.getFeeTypeById(feeTypeId);

        log.info("[Fee] [Resource] getFeeTypeById End");
        log.info("Output: {}", resDTO);

        return Response.ok(resDTO).build();
    }
}
