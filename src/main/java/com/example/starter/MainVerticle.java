package com.example.starter;

import com.example.starter.config.AdminRouter;
import com.example.starter.config.StudentRouter;
import com.example.starter.config.TeacherRouter;
import io.ebean.DB;
import io.ebean.Database;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;




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

    AdminRouter.INSTANCE.router(router);
    StudentRouter.INSTANCE.router(router);
    TeacherRouter.INSTANCE.router(router);

    vertx.createHttpServer().requestHandler(router).listen(8080);

  }

  public static void main(String[] args){
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
