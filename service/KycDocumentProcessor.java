package com.example.starter.service;

import com.example.starter.enums.DocumentType;
import com.example.starter.enums.ValidationStatus;
import com.example.starter.model.KycDocument;
import com.example.starter.repository.KycDocumentRepository;

import java.time.Instant;

public class KycDocumentProcessor {

  private final KycDocumentRepository kycDocumentRepository = new KycDocumentRepository();

  public KycDocument process(
    String userEmail,
    DocumentType documentType,
    String fileName,
    String fileUrl,
    String documentNumber,
    String nameOnDoc,
    String userName
  ){
    ValidationStatus status;
    String message;

    //PAN validation
    if(documentType == DocumentType.PAN){

      if(!KycValidationUtils.isValidPan(documentNumber)){
        status = ValidationStatus.INVALID;
        message = "Invalid PAN format";
      }else if(!KycValidationUtils.isNameMatching(userName, nameOnDoc)){
        status = ValidationStatus.MANUAL_REVIEW;
        message = "Name mismatch";
      }else{
        status = ValidationStatus.VALID;
        message = "Validation Pending by admin";
      }

    }
    //AADHAAR validation
    else if(documentType == DocumentType.AADHAAR){

      if(!KycValidationUtils.isValidAadhaar(documentNumber)){
        status = ValidationStatus.INVALID;
        message = "Invalid AADHAAR format";
      }else if(!KycValidationUtils.isNameMatching(userName, nameOnDoc)){
        status = ValidationStatus.MANUAL_REVIEW;
        message = "Name mismatch";
      }else{
        status = ValidationStatus.VALID;
        message = "Validation pending by admin";
      }

    }
    //PASSPORT validation
    else{
      if(!KycValidationUtils.isValidPassport(documentNumber)){
        status = ValidationStatus.INVALID;
        message = "Invalid PASSPORT format";
      }else if(!KycValidationUtils.isNameMatching(userName, nameOnDoc)){
        status = ValidationStatus.MANUAL_REVIEW;
        message = "Name mismatch";
      }else{
        status = ValidationStatus.VALID;
        message = "Validation pendin by admin";
      }

    }

    //save entity
    KycDocument doc = new KycDocument();
    doc.setUserEmail(userEmail);
    doc.setDocumentType(documentType);
    doc.setFileName(fileName);
    doc.setFileUrl(fileUrl);
    doc.setValidationStatus(status);
    doc.setValidationMessage(message);
    doc.setCreatedAt(Instant.now());
    doc.setUpdatedAt(Instant.now());

    kycDocumentRepository.save(doc);
    return doc;
  }

}
