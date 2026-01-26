package com.example.starter.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public enum StudentJwtAuthHandler implements Handler<RoutingContext> {
  INSTANCE;
  private static final String SECRET_KEY = "sadfkjhbdsfiulhefbasdjfh12312adsf";

  @Override
  public void handle(RoutingContext ctx) {

    String authHeader = ctx.request().getHeader("Authorization");
    if(authHeader==null || !authHeader.startsWith("Bearer ")){
      ctx.response()
        .setStatusCode(401)
        .end("Missing or Invalid token");
      return ;
    }

    String token = authHeader.substring(7);
    try{
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();

      Long userId = claims.get("userId",Long.class);
      String email = claims.get("email", String.class);
      String role = claims.get("role", String.class);

      if (!"STUDENT".equals(role)) {
        ctx.response().setStatusCode(403).end("Forbidden");
        return;
      }

      JsonObject principal = new JsonObject()
        .put("userId", userId)
        .put("email", email)
        .put("role", role);



      ctx.put("userId", userId);
      ctx.put("email", email);
      ctx.put("role", role);

      ctx.next();
    } catch (Exception e) {
      ctx.response()
        .setStatusCode(403)
        .end("Invalid or expired token");
    }
  }
}
