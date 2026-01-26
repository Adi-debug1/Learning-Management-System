package com.example.starter.service;

import com.example.starter.enums.ValidationStatus;

public class ValidationResult {

  private final ValidationStatus status;
  private final String message;

  private ValidationResult(ValidationStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public static ValidationResult valid() {
    return new ValidationResult(ValidationStatus.VALID, "Validation successful");
  }

  public static ValidationResult invalid(String message) {
    return new ValidationResult(ValidationStatus.INVALID, message);
  }

  public static ValidationResult manualReview(String message) {
    return new ValidationResult(ValidationStatus.MANUAL_REVIEW, message);
  }

  public ValidationStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
