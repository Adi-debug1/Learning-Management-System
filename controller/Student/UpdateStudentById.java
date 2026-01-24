package com.example.starter.controller.Student;

import com.example.starter.model.Student;
import com.example.starter.repository.StudentRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;

public enum UpdateStudentById implements Handler<RoutingContext> {
  INSTANCE;
  private StudentRepository studentRepository = new StudentRepository();

  @Override
  public void handle(RoutingContext ctx) {
    Long tokenUserId = Long.valueOf(ctx.get("userId").toString());
    String role = ctx.get("role").toString();
    Long id = Long.valueOf(ctx.pathParam("id"));

    if(!"STUDENT".equals(role)){
      ctx.response()
        .setStatusCode(403)
        .end("Access Denied");
      return;
    }
    if (!tokenUserId.equals(id)) {
      ctx.response()
        .setStatusCode(403)
        .end("You can update only your own profile");
      return;
    }

    ctx.vertx().executeBlocking(()->{
      Student student = studentRepository.findById(id);
      if(student == null){
        throw new RuntimeException("Student not found");
      }

      Student request = ctx.body().asJsonObject().mapTo(Student.class);

      if(request.getPassword()!=null){
        student.setPassword(BCrypt.hashpw(student.getPassword(),BCrypt.gensalt(12)));
      }
      if(request.getFullName() != null){
        student.setFullName(request.getFullName());
      }
      if(request.getMobileNumber() != null){
        student.setMobileNumber(request.getMobileNumber());
      }
      student.setUpdatedAt(Instant.now());
      student.setStatus("Active");

      studentRepository.update(student);
      return student;
    })
      .onSuccess(student->{
        ctx.response()
          .putHeader("Context-Type", "application/json")
          .setStatusCode(200)
          .end(io.vertx.core.json.JsonObject.mapFrom(student).encode());
      })
      .onFailure(err->{
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }
}
