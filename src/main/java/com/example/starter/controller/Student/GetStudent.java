package com.example.starter.controller.Student;

import com.example.starter.repository.StudentRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum GetStudent implements Handler<RoutingContext> {
  INSTANCE;
  private StudentRepository studentRepository = new StudentRepository();

  @Override
  public void handle(RoutingContext ctx) {

    String role = ctx.get("role").toString();
    if(!"ADMIN".equals(role)){
      ctx.response()
        .setStatusCode(403)
        .end("Access Denied");
      return ;
    }

    ctx.vertx().executeBlocking(() -> studentRepository.getAll())
      .onSuccess( students -> {
        ctx.response()
          .putHeader("content-Type", "application/JSON")
          .setStatusCode(200)
          .end(io.vertx.core.json.Json.encode(students));
      })
      .onFailure(err -> {
        ctx.fail(err);
      });
  }
}
