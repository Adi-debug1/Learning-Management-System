package com.example.starter.middleware;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public enum JwtAuth implements Handler<RoutingContext> {
  INSTANCE;

  private static final String SECRET_KEY = "sadfkjhbdsfiulhefbasdjfh12312adsf";

  @Override
  public void handle(RoutingContext ctx) {
    String authHeader = ctx.request().getHeader("Authorization");

    if(authHeader==null || !authHeader.startsWith("Bearer ")){
      ctx.response()
        .setStatusCode(401)
        .end("Token missing");
      return ;
    }

    String token = authHeader.substring(7);
    try{
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();

      ctx.put("userId", claims.get("userId", Long.class));
      ctx.put("role", claims.get("role", String.class));
      ctx.put("email", claims.get("email", String.class));

      ctx.next(); //token valid continue
    }catch (Exception e){
      ctx.response()
        .setStatusCode(401)
        .end("Invalid Token");
    }
  }
}
