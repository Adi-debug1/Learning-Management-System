package com.example.starter.controller.Kyc;

import com.example.starter.ai.AiKycValidationService;
import com.example.starter.enums.DocumentType;
import com.example.starter.enums.Role;
import com.example.starter.enums.ValidationStatus;
import com.example.starter.model.KycDocument;
import com.example.starter.ocr.OcrServiceHolder;
import com.example.starter.repository.KycDocumentRepository;
import com.example.starter.service.FileValidationUtils;
import io.vertx.core.Handler;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.time.Instant;
import java.util.List;

public enum TeacherKycUpload implements Handler<RoutingContext> {
  INSTANCE;

  private final KycDocumentRepository repository = new KycDocumentRepository();
  private AiKycValidationService aiService;

  public void init(io.vertx.core.Vertx vertx) {
    this.aiService = new AiKycValidationService(vertx);
  }

  @Override
  public void handle(RoutingContext ctx) {

    System.out.println("[KYC] StudentKycUpload called");

    String email = ctx.get("email");
    String role = ctx.get("role");

    System.out.println("[KYC] email = " + email);
    System.out.println("[KYC] role = " + role);

    if (email == null || !"TEACHER".equals(role)) {
      System.out.println("[KYC][ERROR] Unauthorized user");
      ctx.fail(401);
      return;
    }

    String docTypeStr = ctx.request().getParam("documentType");
    String docNumber = ctx.request().getParam("documentNumber");
    String fullName = ctx.request().getParam("fullName");

    System.out.println("[KYC] documentType = " + docTypeStr);
    System.out.println("[KYC] documentNumber = " + docNumber);
    System.out.println("[KYC] fullName = " + fullName);

    DocumentType documentType;
    try {
      documentType = DocumentType.valueOf(docTypeStr);
    } catch (Exception e) {
      System.out.println("[KYC][ERROR] Invalid document type");
      ctx.fail(400);
      return;
    }

    List<FileUpload> uploads = ctx.fileUploads();
    if (uploads.isEmpty()) {
      System.out.println("[KYC][ERROR] No file uploaded");
      ctx.fail(400);
      return;
    }

    FileUpload file = uploads.iterator().next();

    System.out.println("[KYC] File uploaded: " + file.fileName());
    System.out.println("[KYC] File path: " + file.uploadedFileName());

    if (!FileValidationUtils.isValidFileType(file.fileName())) {
      System.out.println("[KYC][ERROR] Invalid file type");
      ctx.fail(400);
      return;
    }

    if (repository.findByUserEmailAndType(email, documentType) != null) {
      System.out.println("[KYC][ERROR] KYC already exists for this user");
      ctx.fail(409);
      return;
    }

    // ---- SAVE INITIAL KYC (FAST)
    KycDocument doc = new KycDocument();
    doc.setUserEmail(email);
    doc.setRole(Role.STUDENT);
    doc.setDocumentType(documentType);
    doc.setFileName(file.fileName());
    doc.setFileUrl(file.uploadedFileName());
    doc.setValidationStatus(ValidationStatus.PENDING);
    doc.setValidationMessage("AI verification in progress");
    doc.setCreatedAt(Instant.now());

    System.out.println("[KYC] Saving initial document to DB");
    repository.save(doc);
    System.out.println("[KYC] Initial save completed");

    // ---- BACKGROUND OCR + GEMINI
    System.out.println("[KYC] Starting OCR");
    OcrServiceHolder.get()
      .extractText(file)
      .onSuccess(ocrText -> {

        System.out.println("[KYC] OCR success. Text length: " + ocrText.length());

        System.out.println("[KYC] Calling AI validation");
        aiService.validateKyc(
          "STUDENT",
          documentType.name(),
          fullName,
          docNumber,
          ocrText,
          aiResult -> {

            System.out.println("[KYC] AI result received");
            System.out.println("[KYC] AI Result JSON: " + aiResult.encodePrettily());

            // 1) UPDATE AI RESULT IN DB (now record exists)
            doc.setValidationStatus(ValidationStatus.VALID);
            doc.setValidationMessage(aiResult.encode());

            System.out.println("[KYC] Updating document in DB");
            repository.update(doc);
            System.out.println("[KYC] DB update completed");

            // 2) SEND AI RESULT TO CLIENT
            ctx.response()
              .putHeader("Content-Type", "application/json")
              .end(
                new io.vertx.core.json.JsonObject()
                  .put("status", doc.getValidationStatus())
                  .put("aiResult", aiResult)
                  .encode()
              );
          }
        );

      })
      .onFailure(err -> {
        System.out.println("[KYC][ERROR] OCR failed: " + err.getMessage());

        doc.setValidationStatus(ValidationStatus.MANUAL_REVIEW);
        doc.setValidationMessage("OCR failed");

        System.out.println("[KYC] Updating DB after OCR failure");
        repository.update(doc);

        ctx.response()
          .putHeader("Content-Type", "application/json")
          .end(
            new io.vertx.core.json.JsonObject()
              .put("status", doc.getValidationStatus())
              .put("message", "OCR failed")
              .encode()
          );
      });

  }

}
