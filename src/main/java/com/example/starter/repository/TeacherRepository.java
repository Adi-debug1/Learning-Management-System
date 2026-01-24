package com.example.starter.repository;

import com.example.starter.model.Teacher;
import io.ebean.DB;

import java.util.List;

public class TeacherRepository {

  public void save(Teacher subject){
    subject.save();
  }

  public List<Teacher> getAll(){
    return DB.find(Teacher.class).findList();
  }

  public Teacher findById(long id){
    return DB.find(Teacher.class, id);
  }

  public void deleteById(long id){ DB.delete(Teacher.class, id); }

  public void update(Teacher teacher){ teacher.update(); }

  public Teacher findByEmail(String email){
    return DB.find(Teacher.class)
      .where()
      .eq("email", email)
      .findOne();
  }

}
