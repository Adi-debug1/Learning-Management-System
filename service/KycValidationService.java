package com.example.starter.service;

import com.example.starter.enums.DocumentType;
import com.example.starter.enums.ValidationStatus;

import java.util.Set;
import java.util.regex.Pattern;

public class KycValidationService {

  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
  private static final Set<String> ALLOWED_EXTENSIONS =
    Set.of("pdf", "jpg", "jpeg", "png");

  // PAN: ABCDE1234F
  private static final Pattern PAN_PATTERN =
    Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");

  // Aadhaar: 12 digits
  private static final Pattern AADHAAR_PATTERN =
    Pattern.compile("\\d{12}");

  // Indian Passport: A1234567
  private static final Pattern PASSPORT_PATTERN =
    Pattern.compile("[A-Z][0-9]{7}");

  public ValidationResult validate(
    DocumentType type,
    String documentNumber,
    String userName,
    String fileName,
    long fileSize
  ) {

    // 1️⃣ File size
    if (fileSize > MAX_FILE_SIZE) {
      return ValidationResult.invalid("File size exceeds 5MB limit");
    }

    // 2️⃣ File extension
    String extension = getExtension(fileName);
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      return ValidationResult.invalid("Unsupported file type");
    }

    // 3️⃣ Document-specific validation
    switch (type) {

      case PAN:
        if (!PAN_PATTERN.matcher(documentNumber).matches()) {
          return ValidationResult.invalid("Invalid PAN format");
        }

        // basic name match
        if (!userName.toUpperCase().contains(documentNumber.substring(0, 5))) {
          return ValidationResult.manualReview("PAN name mismatch");
        }

        return ValidationResult.valid();

      case AADHAAR:
        if (!AADHAAR_PATTERN.matcher(documentNumber).matches()) {
          return ValidationResult.invalid("Invalid Aadhaar number");
        }
        return ValidationResult.valid();

      case PASSPORT:
        if (!PASSPORT_PATTERN.matcher(documentNumber).matches()) {
          return ValidationResult.invalid("Invalid passport number");
        }
        return ValidationResult.valid();

      default:
        return ValidationResult.invalid("Unsupported document type");
    }
  }

  private String getExtension(String fileName) {
    int dot = fileName.lastIndexOf(".");
    return dot == -1 ? "" : fileName.substring(dot + 1).toLowerCase();
  }
}
