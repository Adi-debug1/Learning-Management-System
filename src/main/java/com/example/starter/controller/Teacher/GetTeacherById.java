package com.example.starter.controller.Teacher;

import com.example.starter.repository.TeacherRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum GetTeacherById implements Handler<RoutingContext> {
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

    long id;
    try{
      id = Long.parseLong(ctx.pathParam("id"));
    } catch (NumberFormatException e) {
      ctx.response()
        .setStatusCode(400)
        .end("Invalid Subject Id");
      return ;
    }

    ctx.vertx().executeBlocking(()->{
        return subjectRepository.findById(id);
      })
      .onSuccess(subject -> {
        if(subject == null){
          ctx.response()
            .setStatusCode(404)
            .end("Subject Not Found");
        }

        ctx.response()
          .putHeader("Content-Type", "Application/JSON")
          .setStatusCode(200)
          .end(io.vertx.core.json.Json.encode(subject));
      })
      .onFailure(err -> {
        err.printStackTrace();
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }
}
