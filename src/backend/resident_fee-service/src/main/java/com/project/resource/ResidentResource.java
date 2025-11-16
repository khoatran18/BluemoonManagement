package com.project.resource;

import com.project.dto.*;
import com.project.exception.BadRequestException;
import com.project.exception.BusinessException;
import com.project.service.ResidentService;
import main.java.com.project.exception.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.enterprise.context.RequestScoped;

@Path("/api/v1/residents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ResidentResource {

    @Inject
    ResidentService residentService;

    // GET list
    @GET
    public Response listResidents(
            @QueryParam("apartment_id") Long apartmentId,
            @QueryParam("full_name") String fullName,
            @QueryParam("phone_number") String phoneNumber,
            @QueryParam("email") String email,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        try {
            ResidentListResponseDTO dto =
                    residentService.list(apartmentId, fullName, phoneNumber, email, page, limit);

            return Response.ok(ApiResponse.ok(dto)).build();

        } catch (Exception e) {
            return Response.status(500)
                    .entity(ApiResponse.error(500, "Failed to list residents"))
                    .build();
        }
    }

    // GET detail
    @GET
    @Path("{residentId}")
    public Response getResident(@PathParam("residentId") Long residentId) {
        try {
            ResidentDetailsDTO dto = residentService.getDetails(residentId);
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
    public Response createResident(
            @QueryParam("apartment_id") Long apartmentId,
            ResidentCreateDTO req
    ) {
        try {
            ResidentDetailsDTO dto = residentService.create(req, apartmentId);
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
    @Path("{residentId}")
    public Response updateResident(
            @PathParam("residentId") Long residentId,
            @QueryParam("apartment_id") Long apartmentId,
            ResidentUpdateDTO req
    ) {
        try {
            ResidentDetailsDTO dto = residentService.update(residentId, req, apartmentId);
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
    @Path("{residentId}")
    public Response deleteResident(@PathParam("residentId") Long residentId) {
        try {
            residentService.delete(residentId);
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
