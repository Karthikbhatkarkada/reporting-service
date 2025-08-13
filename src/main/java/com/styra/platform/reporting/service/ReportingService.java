package com.styra.platform.reporting.service;

import com.styra.platform.reporting.entity.Report;
import com.styra.platform.reporting.dto.ReportDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReportingService {
    
    private static final Logger LOG = Logger.getLogger(ReportingService.class);
    
    @Inject
    ObjectMapper objectMapper;
    
    // Token management for external reporting modules
    private static final String TOKEN_PREFIX = "RPT-";
    private static final long TOKEN_VALIDITY_HOURS = 24;
    
    @Transactional
    public Report createReport(ReportDTO dto) {
        LOG.infof("Creating report: %s", dto.getName());
        
        Report report = new Report();
        report.name = dto.getName();
        report.reportType = dto.getReportType();
        report.description = dto.getDescription();
        report.templateId = dto.getTemplateId();
        report.parameters = convertMapToJson(dto.getParameters());
        report.outputFormat = dto.getOutputFormat();
        report.scheduleConfig = convertMapToJson(dto.getScheduleConfig());
        report.isScheduled = dto.getIsScheduled();
        report.status = dto.getStatus() != null ? dto.getStatus() : "ACTIVE";
        report.createdBy = dto.getCreatedBy();
        report.businessUnitId = dto.getBusinessUnitId();
        
        report.persist();
        LOG.infof("Created report with ID: %d", report.id);
        return report;
    }
    
    public ReportDTO getReport(Long id) {
        Report report = Report.findById(id);
        if (report == null) {
            return null;
        }
        return convertToDTO(report);
    }
    
    public List<ReportDTO> listReports() {
        List<Report> reports = Report.listAll();
        return reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Report runReport(Long id) {
        Report report = Report.findById(id);
        if (report == null) {
            throw new IllegalArgumentException("Report not found: " + id);
        }
        
        LOG.infof("Running report: %s", report.name);
        report.lastRunAt = java.time.LocalDateTime.now();
        
        // Here you would integrate with actual reporting engine
        // For now, just update the status
        
        return report;
    }
    
    // MI Report Export (Material Inventory Report)
    public String exportMIReport(String fromDate, String toDate, List<Integer> locationRids, 
                                 Integer userRid, Integer locationRid, Integer roleRid, Integer fmRid) {
        LOG.infof("Exporting MI Report from %s to %s", fromDate, toDate);
        
        // Generate report file path
        String fileName = "MI_Report_" + System.currentTimeMillis() + ".xlsx";
        String filePath = "/tmp/reports/" + fileName;
        
        // Here would be the actual report generation logic
        // For now, return the file path
        LOG.infof("Generated MI Report: %s", filePath);
        return filePath;
    }
    
    // CI Report Export (Configuration Item Report)
    public String exportCIReport(String fromDate, String toDate, List<Integer> locationRids,
                                Integer userRid, Integer locationRid, Integer roleRid, Integer fmRid) {
        LOG.infof("Exporting CI Report from %s to %s", fromDate, toDate);
        
        // Generate report file path
        String fileName = "CI_Report_" + System.currentTimeMillis() + ".xlsx";
        String filePath = "/tmp/reports/" + fileName;
        
        // Here would be the actual report generation logic
        // For now, return the file path
        LOG.infof("Generated CI Report: %s", filePath);
        return filePath;
    }
    
    // Generate token for external reporting modules (e.g., Pentaho)
    public Map<String, Object> generateTokenForExternalModule(Integer userRid, Integer emRid) {
        LOG.infof("Generating token for external module: %d, user: %d", emRid, userRid);
        
        String token = TOKEN_PREFIX + userRid + "-" + emRid + "-" + System.currentTimeMillis();
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("token", token);
        response.put("expiresIn", TOKEN_VALIDITY_HOURS * 3600); // in seconds
        response.put("moduleId", emRid);
        
        return response;
    }
    
    // Generate token for document reporting
    public Map<String, Object> generateTokenForDocument(Integer userRid) {
        LOG.infof("Generating token for document reporting, user: %d", userRid);
        
        String token = TOKEN_PREFIX + "DOC-" + userRid + "-" + System.currentTimeMillis();
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("token", token);
        response.put("expiresIn", TOKEN_VALIDITY_HOURS * 3600);
        response.put("type", "document");
        
        return response;
    }
    
    // Validate Pentaho token
    public boolean validatePentahoToken(String token) {
        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            return false;
        }
        
        // Parse token to check expiry
        String[] parts = token.split("-");
        if (parts.length < 4) {
            return false;
        }
        
        try {
            long timestamp = Long.parseLong(parts[parts.length - 1]);
            long currentTime = System.currentTimeMillis();
            long expiryTime = timestamp + (TOKEN_VALIDITY_HOURS * 3600 * 1000);
            
            return currentTime < expiryTime;
        } catch (NumberFormatException e) {
            LOG.error("Invalid token format: " + token);
            return false;
        }
    }
    
    private ReportDTO convertToDTO(Report report) {
        return ReportDTO.builder()
                .id(report.id)
                .name(report.name)
                .reportType(report.reportType)
                .description(report.description)
                .templateId(report.templateId)
                .parameters(parseJsonToMap(report.parameters))
                .outputFormat(report.outputFormat)
                .scheduleConfig(parseJsonToMap(report.scheduleConfig))
                .isScheduled(report.isScheduled)
                .lastRunAt(report.lastRunAt)
                .nextRunAt(report.nextRunAt)
                .status(report.status)
                .createdBy(report.createdBy)
                .businessUnitId(report.businessUnitId)
                .createdAt(report.createdAt)
                .updatedAt(report.updatedAt)
                .build();
    }
    
    private String convertMapToJson(Map<String, Object> map) {
        if (map == null) return null;
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            LOG.error("Error converting map to JSON", e);
            return "{}";
        }
    }
    
    private Map<String, Object> parseJsonToMap(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            LOG.error("Error parsing JSON to map", e);
            return null;
        }
    }
}