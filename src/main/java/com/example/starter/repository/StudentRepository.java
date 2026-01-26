package com.example.starter.repository;


import com.example.starter.model.Student;
import io.ebean.DB;

import java.util.List;

public class StudentRepository {

  public void save(Student student){
    student.save();
  }

  public List<Student> getAll(){
    return DB.find(Student.class).findList();
  }

  public Student findById(long id){
    return DB.find(Student.class, id);
  }

  public boolean delete(Student student){
    if(student == null) return false;
    DB.delete(student);
    return true;
  }

  public void update(Student student){ student.update(); }

  public Student findByEmail(String email){
    return DB.find(Student.class)
      .where()
      .eq("email", email)
      .findOne();
  }

}
