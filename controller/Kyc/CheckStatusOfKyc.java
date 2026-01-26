package com.example.starter.controller.Kyc;

import com.example.starter.model.KycDocument;
import com.example.starter.repository.KycDocumentRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum CheckStatusOfKyc implements Handler<RoutingContext> {
  INSTANCE;
  private final KycDocumentRepository repository = new KycDocumentRepository();

  @Override
  public void handle(RoutingContext ctx) {
    String userEmail = ctx.get("email");

    if (userEmail == null) {
      ctx.response()
        .setStatusCode(401)
        .end("Invalid or expired token");
      return;
    }

    KycDocument doc = repository.findByUserEmail(userEmail);

    if (doc == null) {
      ctx.response()
        .setStatusCode(404)
        .end("KYC not found");
      return;
    }

    ctx.json(
      new io.vertx.core.json.JsonObject()
        .put("kycId", doc.getId())
        .put("email", userEmail)
        .put("status", doc.getValidationStatus())
        .put("message", doc.getValidationMessage())
        .put("documentType",doc.getDocumentType())
    );
  }
}
