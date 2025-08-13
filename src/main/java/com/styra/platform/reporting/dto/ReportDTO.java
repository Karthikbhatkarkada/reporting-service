package com.styra.platform.reporting.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private String name;
    private String reportType;
    private String description;
    private Long templateId;
    private Map<String, Object> parameters;
    private String outputFormat;
    private Map<String, Object> scheduleConfig;
    private Boolean isScheduled;
    private LocalDateTime lastRunAt;
    private LocalDateTime nextRunAt;
    private String status;
    private Long createdBy;
    private Long businessUnitId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}