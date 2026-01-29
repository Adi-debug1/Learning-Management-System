package com.example.starter.controller.Ocr;

import com.example.starter.ocr.OcrSpaceService;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public enum OcrController implements Handler<RoutingContext> {
  INSTANCE;
  private OcrSpaceService ocrSpaceService;

  //call this once during app startup
  public void init(Vertx vertx){
    this.ocrSpaceService = new OcrSpaceService(vertx);
  }

  @Override
  public void handle(RoutingContext ctx) {
    if(ctx.fileUploads().isEmpty()){
      ctx.response()
        .setStatusCode(400)
        .end("File is required");
      return;
    }

    FileUpload upload = ctx.fileUploads().iterator().next();

    ocrSpaceService.extractText(upload)
      .onSuccess(text->{
        ctx.response()
          .putHeader("Content-Type", "application/json")
          .end(new JsonObject()
            .put("text", text)
            .encode());
      })
      .onFailure(err-> ctx.fail(500, err));
  }
}
