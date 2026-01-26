package com.example.starter.controller.Kyc;

import com.example.starter.enums.DocumentType;
import com.example.starter.model.KycDocument;
import com.example.starter.service.FileValidationUtils;
import com.example.starter.service.KycDocumentProcessor;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum KycUpload implements Handler<RoutingContext> {
  INSTANCE;
  private final KycDocumentProcessor processor = new KycDocumentProcessor();

  @Override
  public void handle(RoutingContext ctx) {

    try{
      long userId = ctx.get("userId");

      String documentTypeStr = ctx.request().getParam("documentType");
      String documentNumber = ctx.request().getParam("documentNumber");
      String nameOnDoc = ctx.request().getParam("nameOnDoc");
      String userName = ctx.request().getParam("userName");

      String fileName = ctx.request().getParam("fileName");
      String fileUrl = ctx.request().getParam("fileUrl");

      //basic check
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

      DocumentType documentType = DocumentType.valueOf(documentTypeStr);

      KycDocument doc = processor.process(
        userId,
        documentType,
        fileName,
        fileUrl,
        documentNumber,
        nameOnDoc,
        userName
      );

      //immediate response to uploader
      ctx.response()
        .setStatusCode(200)
        .end(
          "Validation Status: " +
            doc.getValidationStatus() +
            " | Message: " +
            doc.getValidationMessage()
        );
    } catch (Exception e) {
      e.printStackTrace();
      ctx.response()
        .setStatusCode(500)
        .end("KYC upload failed");
    }
  }
}
