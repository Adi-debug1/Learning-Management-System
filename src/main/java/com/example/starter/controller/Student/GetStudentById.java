package com.example.starter.controller.Student;

import com.example.starter.repository.StudentRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum GetStudentById implements Handler<RoutingContext> {
  INSTANCE;
  private StudentRepository studentRepository = new StudentRepository();

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
        .end("Invalid Student Id");
      return ;
    }


    ctx.vertx().executeBlocking(()-> {
        return studentRepository.findById(id);
      })
      .onSuccess(student -> {
        if(student == null) {
          ctx.response()
            .setStatusCode(404)
            .end("Student Not Found");
          return;
        }
        ctx.response()
          .putHeader("Content-Type", "application/JSON")
          .setStatusCode(200)
          .end(io.vertx.core.json.Json.encode(student));
      })
      .onFailure(err -> {
        err.printStackTrace();
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }
}
