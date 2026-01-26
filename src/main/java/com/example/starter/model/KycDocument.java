package com.example.starter.model;

import com.example.starter.enums.DocumentType;
import com.example.starter.enums.Role;
import com.example.starter.enums.ValidationStatus;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
  name = "kyc_document",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "documentType"})
  }
)
public class KycDocument extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long userId;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Enumerated(EnumType.STRING)
  private DocumentType documentType;

  private String fileName;
  private String fileUrl;

  @Enumerated(EnumType.STRING)
  private ValidationStatus validationStatus;

  private String validationMessage;

  // Admin override
  private Long overriddenBy;
  private Instant overriddenAt;
  private String overrideReason;

  @WhenCreated
  private Instant createdAt;

  @WhenModified
  private Instant updatedAt;

  //getter and setter
  public long getId() { return id; }
  public void setId(long id) { this.id = id; }

  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }

  public long getUserId() { return userId; }
  public void setUserId(long userId) { this.userId = userId; }

  public DocumentType getDocumentType() { return documentType; }
  public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }

  public String getFileName() { return fileName; }
  public void setFileName(String fileName) { this.fileName = fileName; }

  public String getFileUrl() { return fileUrl; }
  public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

  public ValidationStatus getValidationStatus() { return validationStatus; }
  public void setValidationStatus(ValidationStatus validationStatus) { this.validationStatus = validationStatus; }

  public String getValidationMessage() { return validationMessage; }
  public void setValidationMessage(String validationMessage) { this.validationMessage = validationMessage; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

  public String getOverrideReason() { return overrideReason; }
  public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }

  public Instant getOverriddenAt() { return overriddenAt; }
  public void setOverriddenAt(Instant overriddenAt) { this.overriddenAt = overriddenAt; }

  public Long getOverriddenBy() { return overriddenBy; }
  public void setOverriddenBy(Long overriddenBy) { this.overriddenBy = overriddenBy; }



}
