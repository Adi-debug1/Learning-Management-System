package com.example.starter.controller.Kyc;

import com.example.starter.enums.DocumentType;
import com.example.starter.enums.Role;
import com.example.starter.enums.ValidationStatus;
import com.example.starter.model.KycDocument;
import com.example.starter.repository.KycDocumentRepository;
import com.example.starter.service.FileValidationUtils;
import com.example.starter.service.KycValidationService;
import com.example.starter.service.ValidationResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.time.Instant;
import java.util.List;

public enum StudentKycUpload implements Handler<RoutingContext> {
  INSTANCE;

  private final KycValidationService validationService = new KycValidationService();
  private final KycDocumentRepository repository = new KycDocumentRepository();

  @Override
  public void handle(RoutingContext ctx) {

    String userEmail = ctx.get("email");
    String role = ctx.get("role");

    if (userEmail == null || !"STUDENT".equals(role)) {
      ctx.fail(401);
      return;
    }


    String documentTypeStr = ctx.request().getParam("documentType");
    String documentNumber = ctx.request().getParam("documentNumber");
    String fileName = ctx.request().getParam("fileName");
    String fullName = ctx.request().getParam("fullName");

    DocumentType documentType;
    try {
      documentType = DocumentType.valueOf(documentTypeStr);
    } catch (Exception e) {
      ctx.fail(400);
      return;
    }

    List<FileUpload> uploads = ctx.fileUploads();
    if (uploads.isEmpty()) {
      ctx.fail(400);
      return;
    }

    if(documentTypeStr == null || fileName == null){
      ctx.response()
        .setStatusCode(400)
        .end("Missing required fields");
      return ;
    }
    //file validation
    if(!FileValidationUtils.isValidFileType(fileName)){
      ctx.response()
        .setStatusCode(400)
        .end("Invalid file type");
      return ;
    }

    FileUpload file = uploads.iterator().next();

    // Validate
    ValidationResult result = validationService.validate(
      documentType,
      documentNumber,
      fullName,
      file.fileName(),
      file.size()
    );

    //  prevent duplicate document
    if (repository.findByUserEmailAndType(userEmail, documentType) != null) {
      ctx.fail(409);
      return;
    }

    // Save document
    KycDocument doc = new KycDocument();
    doc.setUserEmail(userEmail);
    doc.setRole(Role.STUDENT);
    doc.setDocumentType(documentType);
    doc.setFileName(file.fileName());
    doc.setFileUrl(file.uploadedFileName());
    doc.setValidationStatus(ValidationStatus.PENDING);
    doc.setValidationMessage("Pending for admin verification");
    doc.setCreatedAt(Instant.now());

    repository.save(doc);

    // Response
    ctx.json(
      new io.vertx.core.json.JsonObject()
        .put("documentType", documentType)
        .put("status", ValidationStatus.PENDING)
        .put("message", "Pending for admin verification")
    );
  }
}
