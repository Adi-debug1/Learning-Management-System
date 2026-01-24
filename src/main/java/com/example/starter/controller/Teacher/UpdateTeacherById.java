package com.example.starter.controller.Teacher;

import com.example.starter.model.Teacher;
import com.example.starter.repository.TeacherRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;

public enum UpdateTeacherById implements Handler<RoutingContext> {
  INSTANCE;
  private TeacherRepository teacherRepository = new TeacherRepository();

  @Override
  public void handle(RoutingContext ctx) {
    Long tokenUserId = Long.valueOf(ctx.get("userId").toString());
    String role = ctx.get("role").toString();
    long id =Long.parseLong(ctx.pathParam("id"));

    if(!"TEACHER".equals(role)){
      ctx.response()
        .setStatusCode(401)
        .end("Access Denied");
      return ;
    }
    if(!tokenUserId.equals(id)){
      ctx.response()
        .setStatusCode(401)
        .end("You can update your own profile only!");
      return ;
    }

    ctx.vertx().executeBlocking(()->{
      Teacher teacher = teacherRepository.findById(id);
      if(teacher==null){
        throw new RuntimeException("Teacher not found");
      }

      Teacher request = ctx.body().asJsonObject().mapTo(Teacher.class);
      if(request.getPassword()!=null){
        teacher.setPassword(BCrypt.hashpw(teacher.getPassword(), BCrypt.gensalt(12)));
      }
      if(request.getFullName()!=null){
        teacher.setFullName(request.getFullName());
      }
      if(request.getMobileNumber()!=null){
        teacher.setMobileNumber(request.getMobileNumber());
      }
      teacher.setUpdatedAt(Instant.now());
      teacher.setStatus("Active");

      teacherRepository.update(teacher);
      return teacher;
    })
      .onSuccess(teacher->{
        ctx.response()
          .putHeader("Content-Type", "application/json")
          .setStatusCode(200)
          .end(io.vertx.core.json.JsonObject.mapFrom(teacher).encode());
      })
      .onFailure(err->{
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }
}
