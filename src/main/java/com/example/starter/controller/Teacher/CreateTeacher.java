package com.example.starter.controller.Teacher;

import com.example.starter.enums.Role;
import com.example.starter.model.Teacher;
import com.example.starter.repository.TeacherRepository;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;

public enum CreateTeacher implements Handler<RoutingContext> {
  INSTANCE;
  private TeacherRepository subjectRepository = new TeacherRepository();

  @Override
  public void handle(RoutingContext ctx) {
    String role = ctx.get("role").toString();
    if(!"ADMIN".equals(role)){
      ctx.response()
        .setStatusCode(403)
        .end("Access Denied");
    }

    ctx.vertx().executeBlocking(() -> {
        Teacher teacher = ctx.body().asJsonObject().mapTo(Teacher.class);
        String hashPassword = BCrypt.hashpw(
          teacher.getPassword(),
          BCrypt.gensalt(10)
        );
        teacher.setPassword(hashPassword);
        teacher.setRole(Role.STUDENT);
        teacher.setCreatedAt(Instant.now());

        subjectRepository.save(teacher);
        return teacher;
      })
      .onSuccess(
        teacher -> {
          ctx.response()
            .putHeader("Content-type", "application/json")
            .setStatusCode(200)
            .end(io.vertx.core.json.JsonObject.mapFrom(teacher).encode());
        }
      )
      .onFailure( err ->{
          ctx.fail(err);
        }
      );
  }
}
