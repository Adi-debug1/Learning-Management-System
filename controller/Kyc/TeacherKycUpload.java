package com.example.starter.controller.Kyc;

import com.example.starter.enums.DocumentType;
import com.example.starter.enums.Role;
import com.example.starter.model.KycDocument;
import com.example.starter.repository.KycDocumentRepository;
import com.example.starter.service.KycValidationService;
import com.example.starter.service.ValidationResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public enum TeacherKycUpload implements Handler<RoutingContext> {
  INSTANCE;

  private final KycValidationService validationService = new KycValidationService();
  private final KycDocumentRepository repository = new KycDocumentRepository();

  @Override
  public void handle(RoutingContext ctx) {

    long teacherId = ctx.user().principal().getLong("userId");

    String documentTypeStr = ctx.request().getParam("documentType");
    String documentNumber = ctx.request().getParam("documentNumber");
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

    FileUpload file = uploads.iterator().next();

    //  Validate
    ValidationResult result = validationService.validate(
      documentType,
      documentNumber,
      fullName,
      file.fileName(),
      file.size()
    );

    //  Prevent duplicate document
    if (repository.findByUserIdAndType(teacherId, documentType) != null) {
      ctx.fail(409);
      return;
    }

    //  Save document
    KycDocument doc = new KycDocument();
    doc.setUserId(teacherId);
    doc.setRole(Role.TEACHER);
    doc.setDocumentType(documentType);
    doc.setFileName(file.fileName());
    doc.setFileUrl(file.uploadedFileName());
    doc.setValidationStatus(result.getStatus());
    doc.setValidationMessage(result.getMessage());
    doc.setCreatedAt(Instant.now());

    repository.save(doc);

    //  Response
    ctx.json(
      new io.vertx.core.json.JsonObject()
        .put("documentType", documentType)
        .put("status", result.getStatus())
        .put("message", result.getMessage())
    );
  }
}
