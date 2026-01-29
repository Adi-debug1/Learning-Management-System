package com.example.starter.ai;

import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.core.Handler;

public class AiKycValidationService {
  Dotenv dotenv = Dotenv.load();

  private final String OPENROUTER_URL = dotenv.get("AI_URL");
  private final String API_KEY = dotenv.get("AI_API_KEY");
  private final String MODEL = dotenv.get("AI_MODEL");


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
      "You are an automated KYC verification engine.\n" +
        "Your task is to compare user-provided data with OCR-extracted text.\n\n" +

        "STRICT RULES:\n" +
        "- Return ONLY valid JSON\n" +
        "- Do NOT add explanations outside JSON\n" +
        "- Allowed aiRecommendation values: APPROVED, REJECTED, MANUAL_REVIEW\n" +
        "- Prefer MANUAL_REVIEW if there is any uncertainty\n\n" +

        "DECISION LOGIC:\n" +
        "- APPROVED: Name and Number clearly and confidently match OCR text\n" +
        "- REJECTED: Clear and confident mismatch\n" +
        "- MANUAL_REVIEW: OCR is unclear, incomplete, partially matched, or unreliable\n\n" +

        "IF OCR IS UNCLEAR, INCLUDE POSSIBLE REASONS SUCH AS:\n" +
        "- Blurry or low-quality image\n" +
        "- Poor lighting or shadows\n" +
        "- Cropped or partially visible document\n" +
        "- Handwritten or damaged text\n" +
        "- OCR extraction errors\n" +
        "- Language or font issues\n" +
        "- Multiple names or numbers present\n\n" +

        "User Data:\n" +
        "Role: " + role + "\n" +
        "Document Type: " + docType + "\n" +
        "Name: " + name + "\n" +
        "Number: " + number + "\n\n" +

        "OCR Extracted Text:\n" +
        ocrText + "\n\n" +

        "Return JSON in EXACT format:\n" +
        "{\n" +
        "  \"aiRecommendation\": \"APPROVED | REJECTED | MANUAL_REVIEW\",\n" +
        "  \"confidence\": 0-100,\n" +
        "  \"reason\": \"brief explanation mentioning possible causes if unclear\"\n" +
        "}";


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
