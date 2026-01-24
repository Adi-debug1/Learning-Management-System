package com.example.starter.config;

import com.example.starter.controller.Kyc.KycUpload;
import com.example.starter.controller.Kyc.TeacherKycUpload;
import com.example.starter.controller.Teacher.GetTeacherById;
import com.example.starter.controller.Teacher.GetTeacher;
import com.example.starter.controller.Teacher.TeacherLogin;
import com.example.starter.controller.Teacher.UpdateTeacherById;
import com.example.starter.security.TeacherJwtAuthHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public enum TeacherRouter implements Handler<RoutingContext> {
  INSTANCE;

  @Override
  public void handle(RoutingContext event) {
  }

  public void router(Router router){

    //POST Subject
    //router.post("/teacher").handler(CreateTeacher.INSTANCE);
//    //get teacher by id
//    router.get("/teacher/:id").handler(GetTeacherById.INSTANCE);

    System.out.println("Inside the teacher");


    //teacher login
    router.route("/teacher/login").handler(TeacherLogin.INSTANCE);

    //JWT Authentication
    router.route("/teacher/*").handler(TeacherJwtAuthHandler.INSTANCE);

    //KYC upload
    router.post("/teacher/kyc/upload")
      .handler(BodyHandler.create().setUploadsDirectory("uploads"))
      .handler(TeacherKycUpload.INSTANCE);

    //update student by id
    router.put("/teacher/update/:id").handler(TeacherJwtAuthHandler.INSTANCE).handler(UpdateTeacherById.INSTANCE);


  }
}
