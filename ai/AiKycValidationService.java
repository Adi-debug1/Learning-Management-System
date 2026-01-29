package com.example.starter.ai;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.core.Handler;

public class AiKycValidationService {

  private static final String OPENROUTER_URL =
    "https://openrouter.ai/api/v1/chat/completions";

  private static final String API_KEY =
    "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";

  private static final String MODEL =
    "meta-llama/llama-3.1-8b-instruct";

  private final WebClient client;

  public AiKycValidationService(Vertx vertx) {
    this.client = WebClient.create(vertx);
    System.out.println("[AI] Open-source AI initialized");
  }

  public void validateKyc(
    String role,
    String docType,
    String name,
    String number,
    String ocrText,
    Handler<JsonObject> handler
  ) {

    System.out.println("[AI] validateKyc() called");

    String prompt =
      "You are a KYC verification engine.\n" +
        "Return ONLY valid JSON.\n\n" +
        "User Role: " + role + "\n" +
        "Document Type: " + docType + "\n" +
        "Name: " + name + "\n" +
        "Number: " + number + "\n\n" +
        "OCR Text:\n" + ocrText + "\n\n" +
        "JSON format:\n" +
        "{ \"aiRecommendation\": \"APPROVED | REJECTED | MANUAL_REVIEW\", " +
        "\"confidence\": 0-100, " +
        "\"reason\": \"string\" }";

    JsonObject body = new JsonObject()
      .put("model", MODEL)
      .put("temperature", 0.2)
      .put("messages", new JsonArray().add(
        new JsonObject()
          .put("role", "user")
          .put("content", prompt)
      ));

    client.postAbs(OPENROUTER_URL)
      .putHeader("Authorization", "Bearer " + API_KEY)
      .putHeader("Content-Type", "application/json")
      .timeout(20000)
      .sendBuffer(Buffer.buffer(body.encode()))
      .onSuccess(res -> {

        if (res.statusCode() != 200) {
          System.out.println("[AI] Non-200 response");
          handler.handle(defaultManualReview());
          return;
        }

        JsonObject json = res.bodyAsJsonObject();
        String text = json
          .getJsonArray("choices")
          .getJsonObject(0)
          .getJsonObject("message")
          .getString("content")
          .replace("```json", "")
          .replace("```", "")
          .trim();

        handler.handle(new JsonObject(text));
      })
      .onFailure(err -> {
        System.out.println("[AI][ERROR] " + err.getMessage());
        handler.handle(defaultManualReview());
      });
  }

  private JsonObject defaultManualReview() {
    return new JsonObject()
      .put("aiRecommendation", "MANUAL_REVIEW")
      .put("confidence", 0)
      .put("reason", "AI unavailable");
  }
}
