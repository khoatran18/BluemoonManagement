//package com.project.resource;
//
//import com.project.dto.ApartmentDTO;
//import com.project.entity.Apartment;
//import com.project.exception.ApiResponse;
//import com.project.service.ApartmentService;
//import jakarta.inject.Inject;
//import jakarta.validation.Valid;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
//import java.util.List;
//
//@Path("/apartments")
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
//public class ApartmentResource {
//
//    @Inject
//    ApartmentService service;
//
//    @GET
//    public Response getAllApartments() {
//        List<ApartmentDTO> result = service.getAllApartments();
//        return Response.ok(ApiResponse.ok(result)).build();
//    }
//
//    @GET
//    @Path("/{id}")
//    public Response getApartmentById(@PathParam("id") Long id) {
//        ApartmentDTO result = service.getApartmentById(id);
//        return Response.ok(ApiResponse.ok(result)).build();
//    }
//
//    @POST
//    public Response createApartment(@Valid ApartmentDTO dto) {
//        ApartmentDTO created = service.createApartment(dto);
//        return Response.status(Response.Status.CREATED)
//                .entity(ApiResponse.created(created))
//                .build();
//    }
//
//    @PUT
//    @Path("/{id}")
//    public Response updateApartment(@PathParam("id") Long id, @Valid ApartmentDTO dto) {
//        ApartmentDTO updated = service.updateApartment(id, dto);
//        return Response.ok(ApiResponse.ok(updated)).build();
//    }
//
//    @DELETE
//    @Path("/{id}")
//    public Response deleteApartment(@PathParam("id") Long id) {
//        service.deleteApartment(id);
//        return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
//    }
//}