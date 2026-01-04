package com.project.resident_fee_service.resource;

import com.project.common_package.exception.ApiResponse;
import com.project.resident_fee_service.dto.PayHistoryDTO;
import com.project.resident_fee_service.dto.ReportDTO;
import com.project.resident_fee_service.service.PayHistoryService;
import com.project.resident_fee_service.service.ReportService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/reports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    private static final Logger log =
            LoggerFactory.getLogger(ReportResource.class);

    @Inject
    ReportService reportService;

    @GET
    @Path("/apartment_common")
    public Response getApartmentCommonReport() {

        log.info("[Report] [Resource] getApartmentCommonReport Start");
        log.info("Input: None");

        ReportDTO.ApartmentCommonReportDTO resDTO = reportService.getApartmentCommonReport();

        log.info("[Report] [Resource] getApartmentCommonReport End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }

    /////////////////////////////
    // GET DETAIL
    /////////////////////////////

    @GET
    @Path("/fee_common")
    public Response getFeeCommonReport() {

        log.info("[Report] [Resource] getFeeCommonReport Start");
        log.info("Input: None");

        ReportDTO.FeeCommonReportDTO resDTO = reportService.getFeeCommonReport();

        log.info("[Report] [Resource] getFeeCommonReport End");
        log.info("Output: {}", resDTO);

        return Response.ok(ApiResponse.ok(resDTO)).build();
    }
}
