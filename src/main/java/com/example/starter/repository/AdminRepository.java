package com.example.starter.repository;

import com.example.starter.model.Admin;
import io.ebean.DB;

public class AdminRepository {

  public void save(Admin admin){ admin.save(); }

  public Admin findById(long id){
    return DB.find(Admin.class, id);
  }

  public void update(Admin admin){  admin.update(); }

  public Admin findByEmailId(String email){
    return DB.find(Admin.class)
      .where()
      .eq("email", email)
      .findOne();
  }

}
