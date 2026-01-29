package com.example.starter;

import com.example.starter.config.AdminRouter;
import com.example.starter.config.StudentRouter;
import com.example.starter.config.TeacherRouter;
import com.example.starter.controller.Kyc.StudentKycUpload;
import com.example.starter.controller.Kyc.TeacherKycUpload;
import com.example.starter.controller.Ocr.OcrController;
import com.example.starter.ocr.OcrServiceHolder;
import io.ebean.DB;
import io.ebean.Database;
import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;

import java.sql.SQLOutput;


public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {

    try {
      Database database = DB.getDefault();
      System.out.println("✓ Ebean connected successfully!");
      System.out.println("✓ Database: " + database.name());
    } catch (Exception e) {
      System.err.println("✗ Ebean connection failed: " + e.getMessage());
      return;
    }

    TeacherKycUpload.INSTANCE.init(vertx);
    StudentKycUpload.INSTANCE.init(vertx);
    OcrServiceHolder.init(vertx);

    Router router = Router.router(vertx);

    router.route().handler(
      BodyHandler.create()
        .setUploadsDirectory("file-uploads")
        .setBodyLimit(10 * 1024 * 1024) // 10 MB
    );


//    System.out.println("You are in main file");
    AdminRouter.INSTANCE.router(router);
//    System.out.println("Admin");
    StudentRouter.INSTANCE.router(router);
//    System.out.println("Student");
    TeacherRouter.INSTANCE.router(router);
//    System.out.println("Teacher");

//    OcrController.INSTANCE.init(vertx);
//
//    router.post("/ocr")
//      .handler(BodyHandler.create())
//      .handler(OcrController.INSTANCE);

    JsonObject cfg = vertx.getOrCreateContext().config();

    int port = cfg.getInteger("http.port" , 8080);

    vertx.createHttpServer().requestHandler(router).listen(port).onSuccess(http->{
      System.out.print("Server running on " + port);
    });
  }

  public static void main(String[] args){
    System.setProperty("io.netty.resolver.dns.macos.native", "true");
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
