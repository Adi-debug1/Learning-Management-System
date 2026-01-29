package com.example.starter.ocr;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;

public class OcrSpaceService {

  private static final String OCR_URL = "https://api.ocr.space/parse/image";
  private static final String API_KEY = "772b6458c288957";

  private final WebClient client;
  private final Vertx vertx;

  public OcrSpaceService(Vertx vertx) {
    this.vertx = vertx;
    this.client = WebClient.create(vertx);

    System.out.println("[OCR] OcrSpaceService initialized");
    System.out.println("[OCR] OCR_URL = " + OCR_URL);
    System.out.println("[OCR] API_KEY present = " + (API_KEY != null));
  }

  public Future<String> extractText(FileUpload file) {

    System.out.println("[OCR] extractText() called");
    System.out.println("[OCR] Original filename: " + file.fileName());
    System.out.println("[OCR] Uploaded file path: " + file.uploadedFileName());
    System.out.println("[OCR] Content-Type: " + file.contentType());
    System.out.println("[OCR] File size: " + file.size());

    return vertx.fileSystem()
      .readFile(file.uploadedFileName())

      .onSuccess(buffer -> {
        System.out.println("[OCR] File read successfully");
        System.out.println("[OCR] File buffer size: " + buffer.length());
      })

      .onFailure(err -> {
        System.out.println("[OCR][ERROR] Failed to read file");
        err.printStackTrace();
      })

      .compose(buffer -> {

        System.out.println("[OCR] Creating multipart form");

        MultipartForm form = MultipartForm.create()
          .binaryFileUpload(
            "file",
            file.fileName(),
            buffer,
            file.contentType()
          )
          .attribute("language", "eng")
          .attribute("isOverlayRequired", "false");

        System.out.println("[OCR] Sending request to OCR.space API...");

        long startTime = System.currentTimeMillis();

        return client
          .postAbs(OCR_URL)
          .putHeader("apikey", API_KEY)
          .timeout(5000)
          .sendMultipartForm(form)

          .onSuccess(res -> {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[OCR] OCR API response received");
            System.out.println("[OCR] Response time(ms): " + duration);
            System.out.println("[OCR] HTTP Status: " + res.statusCode());
          })

          .onFailure(err -> {
            System.out.println("[OCR][ERROR] OCR API call failed");
            err.printStackTrace();
          })

          .map(res -> parseResponse(res.bodyAsJsonObject()));
      });
  }

  private String parseResponse(JsonObject body) {

    System.out.println("[OCR] Parsing OCR response");
    System.out.println("[OCR] Full response: " + body.encodePrettily());

    if (body.getBoolean("IsErroredOnProcessing", false)) {
      System.out.println("[OCR][ERROR] OCR processing error");
      System.out.println("[OCR][ERROR] Message: " + body.getString("ErrorMessage"));
      throw new RuntimeException(body.getString("ErrorMessage"));
    }

    JsonArray results = body.getJsonArray("ParsedResults");

    if (results == null || results.isEmpty()) {
      System.out.println("[OCR][ERROR] ParsedResults is empty or null");
      throw new RuntimeException("No text detected");
    }

    String parsedText = results
      .getJsonObject(0)
      .getString("ParsedText");

    System.out.println("[OCR] Parsed text length: " + parsedText.length());
    System.out.println("[OCR] OCR extraction successful");

    return parsedText;
  }
}
