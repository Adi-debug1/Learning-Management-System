package com.example.starter.controller.Kyc;

import com.example.starter.enums.DocumentType;
import com.example.starter.enums.ValidationStatus;
import com.example.starter.model.KycDocument;
import com.example.starter.repository.KycDocumentRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.time.Instant;

public enum KycOverride implements Handler<RoutingContext> {
  INSTANCE;
  private final KycDocumentRepository repository = new KycDocumentRepository();

  @Override
  public void handle(RoutingContext ctx) {

    long adminId = ctx.user().principal().getLong("adminId");

    String userEmail = ctx.request().getParam("email");
    DocumentType documentType ;
    try{
      documentType = DocumentType.valueOf(ctx.request().getParam("documentType"));
    }catch (Exception e){
      ctx.fail(400);
      return ;
    }
    String status = ctx.request().getParam("status");
    String reason = ctx.request().getParam("reason");

    // find document
    KycDocument doc = repository.findByUserEmailAndType(userEmail, documentType);
    if (doc == null) {
      ctx.fail(404);
      return;
    }

    // set status
    try {
      doc.setValidationStatus(ValidationStatus.valueOf(status));
    } catch (Exception e) {
      ctx.fail(400);
      return;
    }

    doc.setValidationMessage(reason);
    doc.setOverriddenBy(adminId);
    doc.setOverriddenAt(Instant.now());

    repository.update(doc);

    ctx.json(
      new io.vertx.core.json.JsonObject()
        .put("message", "Override successful")
        .put("status", doc.getValidationStatus())
    );
  }
}
