package com.example.starter.controller.Student;

import com.example.starter.model.Student;
import com.example.starter.repository.StudentRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum DeleteStudentById implements Handler<RoutingContext> {
  INSTANCE;
  private StudentRepository studentRepository = new StudentRepository();

  @Override
  public void handle(RoutingContext ctx) {
    Object roleObj = ctx.get("role");

    if (roleObj == null || !"ADMIN".equals(roleObj.toString())) {
      ctx.response()
        .setStatusCode(403)
        .end("Access Denied");
      return;
    }

    int id;
    try{
      id = Integer.parseInt(ctx.pathParam("id"));
    } catch (NumberFormatException e) {
      ctx.response()
        .setStatusCode(400)
        .end("Invalid Student Id");
      return ;
    }

    ctx.vertx().executeBlocking(()->{
      Student student = studentRepository.findById(id);

      if(student == null){
        return null;
      }
      studentRepository.delete(student);
      return student;
    }).onSuccess(student -> {

      if (student == null) {
        ctx.response()
          .setStatusCode(404)
          .end("Student Not Found");
        return;
      }

      ctx.response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(200)
        .end("Student deleted successfully");

    }).onFailure(err -> {
      err.printStackTrace();
      ctx.response()
        .setStatusCode(500)
        .end("Internal Server Error");
    });
  }
}
