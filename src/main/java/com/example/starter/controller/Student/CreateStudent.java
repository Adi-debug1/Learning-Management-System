package com.example.starter.controller.Student;

import com.example.starter.enums.Role;
import com.example.starter.model.Student;
import com.example.starter.repository.StudentRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;

public enum CreateStudent implements Handler<RoutingContext> {

  INSTANCE;
  private StudentRepository repo = new StudentRepository();

  @Override
  public void handle(RoutingContext ctx) {
    ctx.vertx().executeBlocking(() -> {
        Student student = ctx.body().asJsonObject().mapTo(Student.class);
        String hashPassword = BCrypt.hashpw(
          student.getPassword(),
          BCrypt.gensalt(10)
        );
        student.setPassword(hashPassword);
        student.setRole(Role.STUDENT);
        student.setCreatedAt(Instant.now());

        repo.save(student);
        return student;
      })
      .onSuccess(
        student -> {
          ctx.response()
            .putHeader("Content-Type" , "application/json")
            .setStatusCode(200)
            .end(io.vertx.core.json.JsonObject.mapFrom(student).encode());
        }
      )
      .onFailure(err ->
        {
          ctx.fail(err);
        }
      );
  }
}
