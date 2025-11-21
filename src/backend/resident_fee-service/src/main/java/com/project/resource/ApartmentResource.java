package com.project.resource;

import com.project.dto.*;
import com.project.exception.BadRequestException;
import com.project.exception.BusinessException;
import main.java.com.project.exception.ApiResponse;
import com.project.service.ApartmentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.enterprise.context.RequestScoped;

@Path("/api/v1/apartments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ApartmentResource {

    @Inject
    ApartmentService apartmentService;

    // GET list
    @GET
    public Response listApartments(
            @QueryParam("building") String building,
            @QueryParam("room_number") String roomNumber,
            @QueryParam("head_resident_id") Long headResidentId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        try {
            ApartmentListResponseDTO dto =
                    apartmentService.list(building, roomNumber, headResidentId, page, limit);

            return Response.ok(ApiResponse.ok(dto)).build();

        } catch (Exception e) {
            return Response.status(500)
                    .entity(ApiResponse.error(500, "Failed to list apartments"))
                    .build();
        }
    }

    // GET detail
    @GET
    @Path("{apartmentId}")
    public Response getApartment(@PathParam("apartmentId") Long apartmentId) {
        try {
            ApartmentDetailsDTO dto = apartmentService.getDetails(apartmentId);
            return Response.ok(ApiResponse.ok(dto)).build();

        } catch (BadRequestException e) {
            return Response.status(404)
                    .entity(ApiResponse.error(404, e.getMessage()))
                    .build();

        } catch (BusinessException e) {
            return Response.status(500)
                    .entity(ApiResponse.error(500, e.getMessage()))
                    .build();
        }
    }

    // POST create
    @POST
    public Response createApartment(ApartmentCreateDTO req) {
        try {
            ApartmentDetailsDTO dto = apartmentService.create(req);
            return Response.status(201).entity(ApiResponse.created(dto)).build();

        } catch (BadRequestException e) {
            return Response.status(400)
                    .entity(ApiResponse.error(400, e.getMessage()))
                    .build();

        } catch (BusinessException e) {
            return Response.status(500)
                    .entity(ApiResponse.error(500, e.getMessage()))
                    .build();
        }
    }

    // PUT update
    @PUT
    @Path("{apartmentId}")
    public Response updateApartment(
            @PathParam("apartmentId") Long apartmentId,
            ApartmentUpdateDTO req
    ) {
        try {
            ApartmentDetailsDTO dto = apartmentService.update(apartmentId, req);
            return Response.ok(ApiResponse.ok(dto)).build();

        } catch (BadRequestException e) {
            return Response.status(400)
                    .entity(ApiResponse.error(400, e.getMessage()))
                    .build();

        } catch (BusinessException e) {
            return Response.status(500)
                    .entity(ApiResponse.error(500, e.getMessage()))
                    .build();
        }
    }

    // DELETE
    @DELETE
    @Path("{apartmentId}")
    public Response deleteApartment(@PathParam("apartmentId") Long apartmentId) {
        try {
            apartmentService.delete(apartmentId);
            return Response.status(204)
                    .entity(ApiResponse.noContent("Deleted success"))
                    .build();

        } catch (BadRequestException e) {
            return Response.status(404)
                    .entity(ApiResponse.error(404, e.getMessage()))
                    .build();

        } catch (BusinessException e) {
            return Response.status(500)
                    .entity(ApiResponse.error(500, e.getMessage()))
                    .build();
        }
    }
}
