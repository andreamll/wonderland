package com.wonderland.immi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class UploadedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    private String documentType;
    private String originalFileName;
    private String bucketObjectName;
    private String bucketUrl;

    private LocalDateTime uploadedAt;

    public UploadedDocument() {}

    public Long getId() { return id; }

    public Long getApplicationId() { return applicationId; }

    public String getDocumentType() { return documentType; }

    public String getOriginalFileName() { return originalFileName; }

    public String getBucketObjectName() { return bucketObjectName; }

    public String getBucketUrl() { return bucketUrl; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public void setId(Long id) { this.id = id; }

    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public void setBucketObjectName(String bucketObjectName) { this.bucketObjectName = bucketObjectName; }

    public void setBucketUrl(String bucketUrl) { this.bucketUrl = bucketUrl; }

    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}