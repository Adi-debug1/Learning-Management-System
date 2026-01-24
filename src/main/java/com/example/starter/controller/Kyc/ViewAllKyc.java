package com.example.starter.controller.Kyc;

import com.example.starter.model.KycDocument;
import com.example.starter.repository.KycDocumentRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public enum ViewAllKyc implements Handler<RoutingContext> {
  INSTANCE, ctx;

  private final KycDocumentRepository repository = new KycDocumentRepository();

  @Override
  public void handle(RoutingContext ctx) {
    ctx.json(repository.findAll());
  }
}
