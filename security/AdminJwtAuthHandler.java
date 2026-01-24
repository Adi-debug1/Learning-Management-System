package com.example.starter.security;

import com.example.starter.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;


public enum AdminJwtAuthHandler implements Handler<RoutingContext> {
  INSTANCE;
  private static final String SECRET_KEY = "sadfkjhbdsfiulhefbasdjfh12312adsf";

  @Override
  public void handle(RoutingContext ctx) {
    String authHeader = ctx.request().getHeader("Authorization");

    if(authHeader==null || !authHeader.startsWith("Bearer ")){
      ctx.response()
        .setStatusCode(401)
        .end("Missing or invalid Authorization header");
      return;
    }

    String token = authHeader.substring(7);

    try{
      //verify token
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();

      //extract data
      Long userId = claims.get("userId", Long.class);
      String role = claims.get("role", String.class);

      //store in context
      ctx.put("userId", userId);
      ctx.put("role", role);

      ctx.next();
    } catch (Exception e) {
      ctx.response()
        .setStatusCode(401)
        .end("Invalid or expired token");
    }
  }
}
