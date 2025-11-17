package com.project.resource;

import com.project.dto.FeeTypeDTO;
import com.project.service.FeeTypeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("api/v1/fee-types")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeeTypeResource {

    @Inject
    FeeTypeService feeTypeService;

    ///////////////////////////// For Get method /////////////////////////////

    /**
     * Retrieve a paginated list of FeeTypes based on optional filter.
     */
    @GET
    public Response getFeeTypesByFilter() {

        // Call service function
        FeeTypeDTO.GetFeeTypesResponseDTO resDTO = feeTypeService.getFeeTypesByFilter();

        // Response
        return Response.ok(resDTO)
                .build();
    }

    /**
     * Retrieve a FeeType record by FeeId
     */
    @GET
    @Path("/{fee_type_id}")
    public Response getFeeTypeById(@PathParam("fee_type_id") Long feeTypeId) {

        // Call service function
        FeeTypeDTO.GetFeeTypeResponseDTO resDTO = feeTypeService.getFeeTypeById(feeTypeId);

        // Response
        return Response.ok(resDTO)
                .build();
    }

}
