package com.example.starter.controller.Teacher;

import com.example.starter.model.Teacher;
import com.example.starter.repository.TeacherRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum DeleteTeacherById implements Handler<RoutingContext> {
  INSTANCE;
  private TeacherRepository teacherRepository = new TeacherRepository();

  @Override
  public void handle(RoutingContext ctx) {
    Object roleObj = ctx.get("role");

    if (roleObj == null || !"ADMIN".equals(roleObj.toString())) {
      ctx.response()
        .setStatusCode(403)
        .end("Access Denied");
      return;
    }

    long id;
    try{
      id = Long.parseLong(ctx.pathParam("id"));
    } catch (NumberFormatException e) {
      ctx.response()
        .setStatusCode(404)
        .end("Invalid Id you are in teacherid");
      System.out.println("Yout in log");
      return ;
    }

    ctx.vertx().executeBlocking(()->{
      Teacher teacher = teacherRepository.findById(id);

      if(teacher == null){
        return null;
      }
      teacherRepository.deleteById(id);
      return teacher;
    })
      .onSuccess(teacher ->{

        if(teacher == null){
          ctx.response()
            .setStatusCode(404)
            .end("Teacher not exist");
          return ;
        }

        ctx.response()
          .putHeader("Content-Type", "Apllication/JSON")
          .setStatusCode(200)
          .end("Teacher deleted successfuly");
      })
      .onFailure(err->{
        err.printStackTrace();
        ctx.response()
          .setStatusCode(505)
          .end("Internal server error");
      });

  }
}
