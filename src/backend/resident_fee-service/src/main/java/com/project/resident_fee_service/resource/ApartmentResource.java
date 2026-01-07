package com.project.resident_fee_service.resource;

import com.project.resident_fee_service.dto.ApartmentDTO.*;
import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.entity.Account;
import com.project.resident_fee_service.middleware.RoleAllowedEx;
import com.project.resident_fee_service.service.ApartmentService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/apartments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ApartmentResource {

        private static final Logger log = LoggerFactory.getLogger(ApartmentResource.class);

        @Inject
        ApartmentService apartmentService;

        ////////////////////////////////////////
        // GET LIST
        ////////////////////////////////////////

        @GET
        @RoleAllowedEx({ Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector })
        public Response getApartmentsByFilter(
                        @QueryParam("building") String building,
                        @QueryParam("room_number") String roomNumber,
                        @QueryParam("head_resident_id") Long headResidentId,
                        @QueryParam("page") @DefaultValue("1") int page,
                        @QueryParam("limit") @DefaultValue("10") int limit) {

                log.info("[Resident] [Resource] getApartmentsByFilter Start");
                log.info("Input: building={}, roomNumber={}, headResidentId={}, page={}, limit={}",
                                building, roomNumber, headResidentId, page, limit);

                ApartmentListResponseDTO resDTO = apartmentService.getApartmentsByFilter(
                                building, roomNumber, headResidentId, page, limit);

                log.info("[Resident] [Resource] getApartmentsByFilter End");
                log.info("Output: {}", resDTO);

                return Response.ok(ApiResponse.ok(resDTO)).build();
        }

        ////////////////////////////////////////
        // GET DETAIL
        ////////////////////////////////////////

        @GET
        @Path("/{apartment_id}")
        public Response getApartmentById(
                        @PathParam("apartment_id") Long apartmentId) {

                log.info("[Resident] [Resource] getApartmentById Start");
                log.info("Input: apartmentId={}", apartmentId);

                ApartmentDetailsDTO resDTO = apartmentService.getApartmentById(apartmentId);

                log.info("[Resident] [Resource] getApartmentById End");
                log.info("Output: {}", resDTO);

                return Response.ok(ApiResponse.ok(resDTO)).build();
        }

        @GET
        @Path("/apartment_specific_adjustments/{apartment_id}")
        public Response getApartmentSpecificAdjustments(
                        @PathParam("apartment_id") Long apartmentId) {

                log.info("[Resident] [Resource] getApartmentSpecificAdjustments Start");
                log.info("Input: apartmentId={}", apartmentId);

                ApartmentSpecificAdjustmentsResponseDTO resDTO = apartmentService
                                .getApartmentSpecificAdjustments(apartmentId);

                log.info("[Resident] [Resource] getApartmentSpecificAdjustments End");
                log.info("Output: {}", resDTO);

                return Response.ok(ApiResponse.ok(resDTO)).build();
        }

        ////////////////////////////////////////
        // CREATE
        ////////////////////////////////////////

        @POST
        @RoleAllowedEx({ Account.RoleEnum.Admin })
        public Response createApartment(
                        @Valid ApartmentCreateDTO dto) {

                log.info("[Resident] [Resource] createApartment Start");
                log.info("Input: {}", dto);

                apartmentService.createApartment(dto);

                log.info("[Resident] [Resource] createApartment End");
                log.info("Output: None");

                return Response.status(Response.Status.CREATED)
                                .entity(ApiResponse.created(dto))
                                .build();
        }

        ////////////////////////////////////////
        // UPDATE
        ////////////////////////////////////////

        @PUT
        @Path("/{apartment_id}")
        @RoleAllowedEx({ Account.RoleEnum.Admin })
        public Response updateApartment(
                        @PathParam("apartment_id") Long apartmentId,
                        @Valid ApartmentUpdateDTO dto) {

                log.info("[Resident] [Resource] updateApartment Start");
                log.info("Input: apartmentId={}, dto={}", apartmentId, dto);

                apartmentService.updateApartment(apartmentId, dto);

                log.info("[Resident] [Resource] updateApartment End");
                log.info("Output: {}", dto);

                return Response.ok(ApiResponse.ok(dto)).build();
        }

        @PUT
        @Path("/apartment_specific_adjustments/{apartment_id}")
        @RoleAllowedEx({ Account.RoleEnum.Admin, Account.RoleEnum.FeeCollector })
        public Response updateApartmentSpecificAdjustments(
                        @PathParam("apartment_id") Long apartmentID,
                        @Valid ApartmentSpecificAdjustmentsRequestDTO dto) {

                log.info("[Resident] [Resource] updateApartmentSpecificAdjustments Start");
                log.info("Input: apartmentId={}, dto={}", apartmentID, dto);

                apartmentService.updateSpecificAdjustments(apartmentID, dto);

                log.info("[Resident] [Resource] updateApartmentSpecificAdjustments End");
                log.info("Output: {}", dto);

                return Response.ok(ApiResponse.ok(dto)).build();
        }

        ////////////////////////////////////////
        // DELETE
        ////////////////////////////////////////

        @DELETE
        @Path("/{apartment_id}")
        @RoleAllowedEx({ Account.RoleEnum.Admin })
        public Response deleteApartment(
                        @PathParam("apartment_id") Long apartmentId) {

                log.info("[Resident] [Resource] deleteApartment Start");
                log.info("Input: apartmentId={}", apartmentId);

                apartmentService.deleteApartment(apartmentId);

                log.info("[Resident] [Resource] deleteApartment End");
                log.info("Output: None");

                return Response.ok(ApiResponse.noContent("Deleted successfully")).build();
        }
}
