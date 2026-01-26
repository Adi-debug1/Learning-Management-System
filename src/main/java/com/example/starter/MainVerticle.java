package com.example.starter;

import com.example.starter.config.AdminRouter;
import com.example.starter.config.StudentRouter;
import com.example.starter.config.TeacherRouter;
import io.ebean.DB;
import io.ebean.Database;
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

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    System.out.println("You are in main file");
    AdminRouter.INSTANCE.router(router);
    System.out.println("Admin");
    StudentRouter.INSTANCE.router(router);
    System.out.println("Student");
    TeacherRouter.INSTANCE.router(router);
    System.out.println("Teacher");

    JsonObject cfg = vertx.getOrCreateContext().config();
    int port = cfg.getInteger("http.port" , 8080);

    vertx.createHttpServer().requestHandler(router).listen(port).onSuccess(http->{
      System.out.print("Server running on " + port);
    });
  }

  public static void main(String[] args){
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
