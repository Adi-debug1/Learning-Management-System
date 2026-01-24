package com.example.starter.controller.BulkUpload;

import com.example.starter.enums.UploadStatus;
import com.example.starter.enums.UploadType;
import com.example.starter.model.BulkUpload;
import com.example.starter.repository.BulkUploadRepository;
import com.example.starter.service.BulkUploadProcessor;
import io.vertx.core.Handler;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public enum StartBulkUpload implements Handler<RoutingContext> {
  INSTANCE;
  private final BulkUploadRepository bulkUploadRepository = new BulkUploadRepository();
  private final BulkUploadProcessor bulkUploadProcessor = new BulkUploadProcessor();

  @Override
  public void handle(RoutingContext ctx) {
    String role = ctx.get("role");
    if (!"ADMIN".equalsIgnoreCase(role)) {
      ctx.response()
        .setStatusCode(403)
        .end("Admin access only");
      return;
    }

    List<FileUpload> uploads = ctx.fileUploads();

    if(uploads.isEmpty()){
      ctx.response()
        .setStatusCode(400)
        .end("CSV file is required");
      return;
    }

    FileUpload fileUpload = uploads.get(0);

    //Admin id from JWT
    Long adminId = ctx.get("userId");
    if(adminId == null){
      ctx.response()
        .setStatusCode(401)
        .end("Unathorized");
      return ;
    }
    BulkUpload bulkUpload = new BulkUpload(adminId, UploadType.STUDENT);

    bulkUpload.setStatus(UploadStatus.IN_PROGRESS);

    bulkUploadRepository.save(bulkUpload);

    String filePath = fileUpload.uploadedFileName();

    ctx.vertx().executeBlocking(() -> {
      bulkUploadProcessor.process(filePath, bulkUpload);
      return null; // REQUIRED
    });



    ctx.response()
      .putHeader("Content-Type", "application/json")
      .end("""
               {
                 "uploadId": "%s",
                 "message": "Bulk upload started"
               }
               """.formatted(bulkUpload.getUploadId()));
  }
}
