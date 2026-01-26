package com.example.starter.controller.Kyc;

import com.example.starter.enums.ValidationStatus;
import com.example.starter.model.KycDocument;
import com.example.starter.repository.KycDocumentRepository;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.time.Instant;

public enum AdminApproveKyc implements Handler<RoutingContext> {
  INSTANCE;
  private final KycDocumentRepository repository = new KycDocumentRepository();

  @Override
  public void handle(RoutingContext ctx) {

    JsonObject json = ctx.body().asJsonObject();
    long kycId = json.getLong("kycId");
    String reason = json.getString("reason", "Approved by admin");

    KycDocument doc = repository.findById(kycId);
    if(doc==null){
      ctx.response().setStatusCode(404).end("KYC not found");
      return ;
    }

    //update status
    doc.setValidationStatus(ValidationStatus.VALID);
    doc.setValidationMessage("Approved by admin");

    //override metada
    Long adminId = ctx.get("userId");
    doc.setOverriddenBy(adminId);
    doc.setOverriddenAt(Instant.now());
    doc.setOverrideReason(reason);

    repository.update(doc);

    ctx.json(
      new JsonObject()
        .put("message", "KYC approved successfully")
        .put("kycId", kycId)
        .put("status", doc.getValidationStatus())
    );
  }
}
