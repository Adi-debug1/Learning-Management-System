package com.example.starter.controller.Student;

import com.example.starter.enums.Role;
import com.example.starter.model.Student;
import com.example.starter.repository.StudentRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

public enum StudentLogin implements Handler<RoutingContext> {
  INSTANCE;
  private StudentRepository studentRepository = new StudentRepository();
  private static final String SECRET_KEY = "sadfkjhbdsfiulhefbasdjfh12312adsf";
  private static final long EXPIRATION_TIME = 15*60*1000;

  @Override
  public void handle(RoutingContext ctx) {
    ctx.vertx().executeBlocking(()->{

      String email = ctx.body().asJsonObject().getString("email");
      String password = ctx.body().asJsonObject().getString("password");

      Student student = studentRepository.findByEmail(email);
      if(student == null){
        throw new RuntimeException("Invalid email");
      }
      if(!BCrypt.checkpw(password, student.getPassword())){
        throw new RuntimeException("Invalid password");
      }
      String token = Jwts.builder()
        .claim("userId", student.getUserId())
        .claim("email", student.getEmail())
        .claim("role", Role.STUDENT.name())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
        .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
        .compact();

      return token;
    })
      .onSuccess(token->{
        ctx.response()
          .putHeader("Content-Type","application/json")
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
