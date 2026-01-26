package com.example.starter.controller.Admin;

import com.example.starter.model.Admin;
import com.example.starter.repository.AdminRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum GetAdminById implements Handler<RoutingContext> {
  INSTANCE;
  private AdminRepository adminRepository = new AdminRepository();

  @Override
  public void handle(RoutingContext ctx) {
    String role = ctx.get("role").toString();
    if(!"ADMIN".equals(role)){
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
          .end("Invalid Id");
        return ;
    }

    ctx.vertx().executeBlocking(()->{
      return adminRepository.findById(id);
    })
      .onSuccess(admin->{
        if(admin == null){
          ctx.response()
            .setStatusCode(404)
            .end("Student not found");
          return ;
        }
        ctx.response()
          .putHeader("content-Type", "Application/JSON")
          .setStatusCode(200)
          .end(io.vertx.core.json.Json.encode(admin));
      })
      .onFailure(err->{
        err.printStackTrace();
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }
}
