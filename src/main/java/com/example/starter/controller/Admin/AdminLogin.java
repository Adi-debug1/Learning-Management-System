package com.example.starter.controller.Admin;

import com.example.starter.model.Admin;
import com.example.starter.repository.AdminRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

public enum AdminLogin implements Handler<RoutingContext> {
  INSTANCE;

  private AdminRepository adminRepository = new AdminRepository();
  private static final String SECRET_KEY = "sadfkjhbdsfiulhefbasdjfh12312adsf";
  private static final long EXPIRATION_TIME = 15*60*1000;

  @Override
  public void handle(RoutingContext ctx) {

    ctx.vertx().executeBlocking(()->{
      String email = ctx.body().asJsonObject().getString("email");
      String password = ctx.body().asJsonObject().getString("password");

      Admin admin = adminRepository.findByEmailId(email);
      if(admin == null){
        throw new RuntimeException("Invalid email id");
      }
      if(!BCrypt.checkpw(password, admin.getPassword())){
        throw new RuntimeException("Invalid password");
      }

      String token = Jwts.builder()
        .claim("userId", admin.getUserId())
        .claim("email", admin.getEmail())
        .claim("role", admin.getRole().name())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
        .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
        .compact();

      return token;
    })
      .onSuccess(token->{
        ctx.response()
          .putHeader("Content-Type", "application/json")
          .setStatusCode(200)
          .end("{\"token\": \"" + token + "\"}");
      })
      .onFailure(err->{
        ctx.response()
          .setStatusCode(401)
          .end(err.getMessage());
      });
  }
}
