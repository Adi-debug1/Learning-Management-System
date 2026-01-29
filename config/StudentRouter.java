package com.example.starter.config;

import com.example.starter.controller.Kyc.CheckStatusOfKyc;
import com.example.starter.controller.Kyc.KycUpload;
import com.example.starter.controller.Kyc.StudentKycUpload;
import com.example.starter.controller.Ocr.OcrController;
import com.example.starter.controller.Student.DeleteStudentById;
import com.example.starter.controller.Student.GetStudentById;
import com.example.starter.controller.Student.StudentLogin;
import com.example.starter.controller.Student.UpdateStudentById;
import com.example.starter.middleware.JwtAuth;
import com.example.starter.security.AdminJwtAuthHandler;
import com.example.starter.security.StudentJwtAuthHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public enum StudentRouter implements Handler<RoutingContext> {
  INSTANCE;

  @Override
  public void handle(RoutingContext event) {

  }
  public void router(Router router){

    //Crate student using enum
//    router.post("/student").handler(CreateStudent.INSTANCE);
    //get All the students
//    router.get("/getStudents").handler(GetStudent.INSTANCE);
//    //get Student by id
//    router.get("/student/:id").handler(GetStudentById.INSTANCE);

    //for login
    router.post("/student/login").handler(StudentLogin.INSTANCE);

    //JWT middleware
//    router.route("/student/*").handler(StudentJwtAuthHandler.INSTANCE);
//
//    //KYC upload
//    router.post("/student/kyc/upload")
//      .handler(BodyHandler.create().setUploadsDirectory("uploads"))
//      .handler(StudentKycUpload.INSTANCE);


    router.route("/student/*")
      .handler(BodyHandler.create()
        .setUploadsDirectory("uploads")
        .setBodyLimit(10 * 1024 * 1024) // 10 MB
      );


    // JWT AFTER body parsing
    router.route("/student/*")
        .handler(StudentJwtAuthHandler.INSTANCE);

  // KYC upload
    router.post("/student/kyc/upload")
      .handler(StudentKycUpload.INSTANCE);
    router.get("/student/check/kyc").handler(CheckStatusOfKyc.INSTANCE);


    //update student
    router.put("/student/update/:id").handler(UpdateStudentById.INSTANCE);
    //Delete the student by id
    router.delete("/student/:id").handler(DeleteStudentById.INSTANCE);

  }
}
