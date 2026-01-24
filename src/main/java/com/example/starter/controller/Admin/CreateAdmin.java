package com.example.starter.controller.Admin;

import com.example.starter.enums.Role;
import com.example.starter.model.Admin;
import com.example.starter.repository.AdminRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;

public enum CreateAdmin implements Handler<RoutingContext> {
  INSTANCE;
  private AdminRepository adminRepository = new AdminRepository();

  @Override
  public void handle(RoutingContext ctx) {


    ctx.vertx().executeBlocking(()->{

      Admin admin = ctx.body().asJsonObject().mapTo(Admin.class);
      //hash password
      String hashPassword = BCrypt.hashpw(
        admin.getPassword(),
        BCrypt.gensalt(10)
      );
      admin.setPassword(hashPassword);
      admin.setRole(Role.ADMIN);
      admin.setCreatedAt(Instant.now());

      adminRepository.save(admin);
      return admin;
    })
      .onSuccess(admin->{
        ctx.response()
          .putHeader("Content-Type","Application/JSON")
          .setStatusCode(200)
          .end(io.vertx.core.json.JsonObject.mapFrom(admin).encode());
      })
      .onFailure(err->{
        ctx.fail(err);
      });
  }
}
