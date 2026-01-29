package com.example.starter.ocr;

import io.vertx.core.Vertx;

public class OcrServiceHolder {

  private static OcrSpaceService INSTANCE;

  public static void init(Vertx vertx) {
    INSTANCE = new OcrSpaceService(vertx);
  }

  public static OcrSpaceService get() {
    return INSTANCE;
  }
}
