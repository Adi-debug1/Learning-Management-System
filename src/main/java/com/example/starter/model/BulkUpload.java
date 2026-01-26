package com.example.starter.model;

import com.example.starter.enums.UploadStatus;
import com.example.starter.enums.UploadType;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bulk_upload")
public class BulkUpload extends Model {

  @Id
  private UUID uploadId;

  // adminId who started the upload
  @Column(nullable = false)
  private Long uploadedBy;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UploadType uploadType;

  private int totalRecords;
  private int successCount;
  private int failureCount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UploadStatus status;

  @WhenCreated
  private Instant createdAt;
  private Instant completedAt;

  public BulkUpload(){}//constructor

  public BulkUpload(Long uploadedBy, UploadType uploadType) {
    this.uploadId = UUID.randomUUID();
    this.uploadedBy = uploadedBy;
    this.uploadType = uploadType;
    this.status = UploadStatus.IN_PROGRESS;
    this.createdAt = Instant.now();
  }

  //Getter adn setters
  public UUID getUploadId(){ return uploadId; }

  public Long getUploadedBy() { return uploadedBy; }
  public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

  public UploadType getUploadType() { return uploadType; }
  public void setUploadType(UploadType uploadType) { this.uploadType = uploadType; }

  public int getTotalRecords() {
    return totalRecords;
  }
  public void setTotalRecords(int totalRecords) {
    this.totalRecords = totalRecords;
  }

  public int getSuccessCount() {
    return successCount;
  }
  public void setSuccessCount(int successCount) {
    this.successCount = successCount;
  }

  public int getFailureCount() {
    return failureCount;
  }
  public void setFailureCount(int failureCount) {
    this.failureCount = failureCount;
  }

  public UploadStatus getStatus() {
    return status;
  }
  public void setStatus(UploadStatus status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(Instant createdAt){ this.createdAt=createdAt; }

  public Instant getCompletedAt() { return completedAt; }
  public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

}
