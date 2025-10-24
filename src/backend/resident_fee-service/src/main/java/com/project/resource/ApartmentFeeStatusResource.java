package main.java.com.project.resource;

import com.project.dto.ApartmentFeeStatusDTO;
import com.project.entity.ApartmentFeeStatus;
import com.project.exception.ApiResponse;
import com.project.service.ApartmentFeeStatusService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/apartment-status")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApartmentFeeStatusResource {

    @Inject
    ApartmentFeeStatusService service;

    @POST
    @Path("/create/{apartmentId}")
    public Response createStatus(@PathParam("apartmentId") Long apartmentId) {
        ApartmentFeeStatusDTO result = service.createStatus(apartmentId);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(result))
                .build();
    }

    @POST
    @Path("/{statusId}/unpaid/{feeId}")
    public Response addUnpaid(@PathParam("statusId") Long statusId,
                              @PathParam("feeId") Long feeId) {
        ApartmentFeeStatusDTO result = service.addUnpaidFee(statusId, feeId);
        return Response.ok(ApiResponse.ok(result)).build();
    }

    @POST
    @Path("/{statusId}/pay/{feeId}")
    public Response markAsPaid(@PathParam("statusId") Long statusId,
                               @PathParam("feeId") Long feeId) {
        ApartmentFeeStatusDTO result = service.markFeeAsPaid(statusId, feeId);
        return Response.ok(ApiResponse.ok(result)).build();
    }

    @GET
    @Path("/apartment/{apartmentId}")
    public Response getByApartment(@PathParam("apartmentId") Long apartmentId) {
        ApartmentFeeStatusDTO result = service.getStatusByApartment(apartmentId);
        return Response.ok(ApiResponse.ok(result)).build();
    }
}
