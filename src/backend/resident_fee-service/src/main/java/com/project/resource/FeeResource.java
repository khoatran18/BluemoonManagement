package main.java.com.project.resource;

import com.project.dto.FeeDTO;
import com.project.entity.Fee;
import com.project.exception.ApiResponse;
import com.project.service.FeeService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/fees")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeeResource {

    @Inject
    FeeService service;

    @GET
    public Response getAllFees() {
        List<FeeDTO> result = service.getAllFees();
        return Response.ok(ApiResponse.ok(result)).build();
    }

    @GET
    @Path("/{id}")
    public Response getFeeById(@PathParam("id") Long id) {
        FeeDTO result = service.getFeeById(id);
        return Response.ok(ApiResponse.ok(result)).build();
    }

    @POST
    public Response createFee(@Valid FeeDTO dto) {
        FeeDTO created = service.createFee(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.created(created))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteFee(@PathParam("id") Long id) {
        service.deleteFee(id);
        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
    }
}
