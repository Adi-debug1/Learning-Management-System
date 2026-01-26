package com.example.starter.config;

import com.example.starter.controller.Admin.AdminLogin;
import com.example.starter.controller.Admin.CreateAdmin;
import com.example.starter.controller.Admin.GetAdminById;
import com.example.starter.controller.Admin.UpdateAdmin;
import com.example.starter.controller.BulkUpload.StartBulkUpload;
import com.example.starter.controller.Kyc.KycOverride;
import com.example.starter.controller.Kyc.ViewAllKyc;
import com.example.starter.controller.Student.CreateStudent;
import com.example.starter.controller.Student.DeleteStudentById;
import com.example.starter.controller.Student.GetStudent;
import com.example.starter.controller.Student.GetStudentById;
import com.example.starter.controller.Teacher.CreateTeacher;
import com.example.starter.controller.Teacher.DeleteTeacherById;
import com.example.starter.controller.Teacher.GetTeacher;
import com.example.starter.controller.Teacher.GetTeacherById;
import com.example.starter.middleware.JwtAuth;
import com.example.starter.security.AdminJwtAuthHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public enum AdminRouter implements Handler<RoutingContext> {
  INSTANCE;

  @Override
  public void handle(RoutingContext event) {

  }

  public void router(Router router){
    // PUBLIC

    router.post("/admin/login").handler(AdminLogin.INSTANCE);


    router.post("/admin/add").handler(CreateAdmin.INSTANCE);
    router.post("/admin/student").handler(CreateStudent.INSTANCE);
    router.post("/admin/teacher").handler(CreateTeacher.INSTANCE);

    // JWT PROTECTION
    router.route("/admin/*").handler(AdminJwtAuthHandler.INSTANCE);


    router.put("/admin/update/:id")
      .handler(UpdateAdmin.INSTANCE);

    // Bulk upload
    router.post("/admin/bulk/upload")
      .handler(StartBulkUpload.INSTANCE);

    // KYC
    router.get("/admin/kyc").handler(ViewAllKyc.INSTANCE);
    router.post("/admin/kyc/override").handler(KycOverride.INSTANCE);

    // fetch users
    router.get("/admin/getStudents").handler(GetStudent.INSTANCE);
    router.get("/admin/getTeachers").handler(GetTeacher.INSTANCE);

    router.get("/admin/:id").handler(GetAdminById.INSTANCE);

    // student
    router.get("/admin/student/:id").handler(GetStudentById.INSTANCE);
    router.delete("/admin/student/:id").handler(DeleteStudentById.INSTANCE);

    // teacher
    router.get("/admin/teacher/:id").handler(GetTeacherById.INSTANCE);
    router.delete("/admin/teacher/:id").handler(DeleteTeacherById.INSTANCE);

  }
}
