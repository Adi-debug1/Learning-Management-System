package com.example.starter.controller.Teacher;

import com.example.starter.repository.TeacherRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum GetTeacher implements Handler<RoutingContext> {
  INSTANCE;
  private TeacherRepository subjectRepository = new TeacherRepository();

  @Override
  public void handle(RoutingContext ctx) {
    String role = ctx.get("role").toString();
    if(!"ADMIN".equals(role)){
      ctx.response()
        .setStatusCode(403)
        .end("Access Denied");
    }

    ctx.vertx().executeBlocking(() -> subjectRepository.getAll())
      .onSuccess( subject ->{
        ctx.response()
          .putHeader("Content-Type", "application/json")
          .setStatusCode(200)
          .end(io.vertx.core.json.Json.encode(subject));
      })
      .onFailure(err ->{
        ctx.fail(err);
      });
  }
}
