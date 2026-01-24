package com.example.starter.controller.Admin;

import com.example.starter.enums.Role;
import com.example.starter.model.Admin;
import com.example.starter.repository.AdminRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;

public enum UpdateAdmin implements Handler<RoutingContext> {
  INSTANCE;
  private AdminRepository repo = new AdminRepository();

  @Override
  public void handle(RoutingContext ctx) {

    Long tokenUserId = Long.valueOf(ctx.get("userId").toString());
    String role = ctx.get("role").toString();
    long adminId = Long.valueOf(ctx.pathParam("id"));

    //Check the role
    if(!"ADMIN".equals(role)){
      ctx.response()
        .setStatusCode(403)
        .end("Access Denied");
      return ;
    }
    //Admin cannot update other admin profile
    if (!tokenUserId.equals(adminId)) {
      ctx.response()
        .setStatusCode(403)
        .end("You can update only your own profile");
      return;
    }

    ctx.vertx().executeBlocking(()->{
      Admin existingAdmin = repo.findById(adminId);

      if(existingAdmin == null){
        throw new RuntimeException("Admin not found");
      }

      Admin request = ctx.body().asJsonObject().mapTo(Admin.class);

        if(request.getPassword() != null){
          existingAdmin.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12)));
        }
        if(request.getFullName() != null){
          existingAdmin.setFullName(request.getFullName());
        }
        if(request.getMobileNumber() != null){
          existingAdmin.setMobileNumber(request.getMobileNumber());
        }
        existingAdmin.setUpdatedAt(Instant.now());
        repo.update(existingAdmin);
        return existingAdmin;
    })
      .onSuccess(admin ->{
        ctx.response()
          .putHeader("Content-Type", "application/json")
          .setStatusCode(200)
          .end(io.vertx.core.json.JsonObject.mapFrom(admin).encode());
      })
      .onFailure(err ->{
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }
}
