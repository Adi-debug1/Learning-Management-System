package com.example.starter.repository;

import com.example.starter.enums.DocumentType;
import com.example.starter.model.KycDocument;
import io.ebean.DB;
import io.ebeaninternal.server.util.Str;

import java.util.List;

public class KycDocumentRepository {

  public void save(KycDocument kycDocument){ kycDocument.save(); }

  public void update(KycDocument kycDocument){ kycDocument.update(); }

  public KycDocument findById(long id){ return DB.find(KycDocument.class, id); }

  public KycDocument findByUserIdAndType(long userId, DocumentType documentType) {
    return DB.find(KycDocument.class)
      .where()
      .eq("userId", userId)
      .eq("documentType", documentType)
      .findOne();
  }

  public List<KycDocument> findAll() {
    return DB.find(KycDocument.class).findList();
  }
}
