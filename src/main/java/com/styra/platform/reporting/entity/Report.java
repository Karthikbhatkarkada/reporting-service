package com.styra.platform.reporting.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report extends PanacheEntity {
    
    @Column(name = "report_name", nullable = false)
    public String name;
    
    @Column(name = "report_type")
    public String reportType;
    
    @Column(name = "description", columnDefinition = "TEXT")
    public String description;
    
    @Column(name = "template_id")
    public Long templateId;
    
    @Column(name = "parameters", columnDefinition = "TEXT")
    public String parameters;
    
    @Column(name = "output_format")
    public String outputFormat;
    
    @Column(name = "schedule_config", columnDefinition = "TEXT")
    public String scheduleConfig;
    
    @Column(name = "is_scheduled")
    public Boolean isScheduled;
    
    @Column(name = "last_run_at")
    public LocalDateTime lastRunAt;
    
    @Column(name = "next_run_at")
    public LocalDateTime nextRunAt;
    
    @Column(name = "status")
    public String status;
    
    @Column(name = "created_by")
    public Long createdBy;
    
    @Column(name = "business_unit_id")
    public Long businessUnitId;
    
    @Column(name = "created_at")
    public LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "ACTIVE";
        }
        if (isScheduled == null) {
            isScheduled = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}