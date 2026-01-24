package com.example.starter.repository;

import com.example.starter.model.BulkUpload;
import io.ebean.DB;

import java.util.Optional;
import java.util.UUID;

public class BulkUploadRepository {

  public void save(BulkUpload bulkUpload){ DB.save(bulkUpload); }

  public void update(BulkUpload bulkUpload){ DB.update(bulkUpload); }

  public Optional<BulkUpload> findById(UUID uploadId){
    return Optional.ofNullable(
      DB.find(BulkUpload.class)
        .where()
        .eq("uploadId",uploadId)
        .findOne()
    );
  }
}
