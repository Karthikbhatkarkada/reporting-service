package com.styra.platform.reporting.resource;

import com.styra.platform.reporting.dto.ReportDTO;
import com.styra.platform.reporting.service.ReportingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.jboss.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Path("/report")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
    
    private static final Logger LOG = Logger.getLogger(ReportResource.class);
    
    @Inject
    ReportingService reportService;
    
    // MI Report Export endpoint matching monolith
    @POST
    @Path("/download-mi-report")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadMIReport(ReportRequestBody body) {
        LOG.infof("Downloading MI Report from %s to %s", body.fromDate, body.toDate);
        
        String filePath = reportService.exportMIReport(
            body.fromDate, body.toDate,
            body.locationRidsList, body.userRid,
            body.locationRid, body.roleRid, body.fmRid
        );
        
        if (filePath == null || filePath.isEmpty()) {
            return Response.serverError()
                    .entity("{\"error\":\"Failed to generate report\"}")
                    .build();
        }
        
        File file = new File(filePath);
        String fileName = file.getName();
        
        StreamingOutput stream = (OutputStream output) -> {
            try (FileInputStream input = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
            }
        };
        
        return Response.ok(stream)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .build();
    }
    
    // CI Report Export endpoint matching monolith
    @POST
    @Path("/download-ci-report")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadCIReport(ReportRequestBody body) {
        LOG.infof("Downloading CI Report from %s to %s", body.fromDate, body.toDate);
        
        String filePath = reportService.exportCIReport(
            body.fromDate, body.toDate,
            body.locationRidsList, body.userRid,
            body.locationRid, body.roleRid, body.fmRid
        );
        
        if (filePath == null || filePath.isEmpty()) {
            return Response.serverError()
                    .entity("{\"error\":\"Failed to generate report\"}")
                    .build();
        }
        
        File file = new File(filePath);
        String fileName = file.getName();
        
        StreamingOutput stream = (OutputStream output) -> {
            try (FileInputStream input = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
            }
        };
        
        return Response.ok(stream)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .build();
    }
    
    // Generate token for external reporting modules
    @GET
    @Path("/generate-token")
    public Response generateTokenForExternalModule(@QueryParam("emRid") Integer emRid,
                                                  @QueryParam("userRid") Integer userRid) {
        Map<String, Object> tokenData = reportService.generateTokenForExternalModule(userRid, emRid);
        return Response.ok(tokenData).build();
    }
    
    // Generate token for document reporting
    @GET
    @Path("/generate-token-for-document")
    public Response generateTokenForDocument(@QueryParam("userRid") Integer userRid) {
        Map<String, Object> tokenData = reportService.generateTokenForDocument(userRid);
        return Response.ok(tokenData).build();
    }
    
    // Validate Pentaho token
    @GET
    @Path("/validate-pentaho-token")
    public Response validatePentahoToken(@HeaderParam("token") String token) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        if (reportService.validatePentahoToken(token)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    // Request body DTO
    public static class ReportRequestBody {
        public String fromDate;
        public String toDate;
        public List<Integer> locationRidsList;
        public Integer userRid;
        public Integer locationRid;
        public Integer roleRid;
        public Integer fmRid;
    }
}