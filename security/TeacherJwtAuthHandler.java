package com.example.starter.security;

import com.example.starter.enums.Role;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum TeacherJwtAuthHandler implements Handler<RoutingContext> {
  INSTANCE;
  Dotenv dotenv = Dotenv.load();
  private final String SECRET_KEY = dotenv.get("SECRET_KEY");

  @Override
  public void handle(RoutingContext ctx) {
    String authHeader = ctx.request().getHeader("Authorization");

    if(authHeader==null || !authHeader.startsWith("Bearer ")){
      ctx.response()
        .setStatusCode(404)
        .end("Missing or invalid authentication");
      return ;
    }

    String token = authHeader.substring(7);
    try{
      Claims claims= Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();

      Long userId = claims.get("userId", Long.class);
      String role = claims.get("role", String.class);
      String email = claims.get("email", String.class);

      ctx.put("userId", userId);
      ctx.put("role", role);
      ctx.put("email", email);

      ctx.next();
    } catch (Exception e) {
      ctx.response()
        .setStatusCode(401)
        .end("Invalid or Expired token");
    }
  }
}
