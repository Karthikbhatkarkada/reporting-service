package com.styra.platform.reporting.resource;

import com.styra.platform.reporting.dto.ReportDTO;
import com.styra.platform.reporting.entity.Report;
import com.styra.platform.reporting.service.ReportingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import java.util.List;

@Path("/api/v1/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportingResource {
    
    private static final Logger LOG = Logger.getLogger(ReportingResource.class);
    
    @Inject
    ReportingService reportingService;
    
    @POST
    public Response createReport(ReportDTO reportDTO) {
        LOG.infof("Creating report: %s", reportDTO.getName());
        Report report = reportingService.createReport(reportDTO);
        return Response.status(Response.Status.CREATED)
                .entity(reportingService.getReport(report.id))
                .build();
    }
    
    @GET
    @Path("/{id}")
    public Response getReport(@PathParam("id") Long id) {
        ReportDTO report = reportingService.getReport(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(report).build();
    }
    
    @GET
    public Response listReports() {
        List<ReportDTO> reports = reportingService.listReports();
        return Response.ok(reports).build();
    }
    
    @POST
    @Path("/{id}/run")
    public Response runReport(@PathParam("id") Long id) {
        try {
            Report report = reportingService.runReport(id);
            return Response.ok(reportingService.getReport(report.id)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    @GET
    @Path("/health")
    public Response health() {
        return Response.ok().entity("{\"status\":\"UP\"}").build();
    }
}