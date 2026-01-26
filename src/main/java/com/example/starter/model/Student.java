package com.example.starter.model;

import com.example.starter.enums.Role;
import io.ebean.Model;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "students")
public class Student extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;
    private String password;

    @Column(length = 10)
    private String mobileNumber;

    private String status ;//(ACTIVE / INACTIVE)
    private Instant createdAt;
    private Instant updatedAt;


    public long getUserId(){
      return userId;
    }
    public void setUserId(long userId){
    this.userId=userId;
  }

    public Role getRole(){ return role; }
    public void setRole(Role role){ this.role=role; }

    public String getFullName(){  return fullName;  }
    public void setFullName(String fullName){ this.fullName=fullName; }

    public String getEmail(){  return email;  }
    public void setEmail(String email){ this.email=email; }
    public String getPassword(){ return password; }
    public void setPassword(String password){ this.password=password; }

    public String getMobileNumber(){  return mobileNumber; }
    public void setMobileNumber(String mobileNumber){ this.mobileNumber=mobileNumber; }

    public String getStatus(){  return status;  }
    public void setStatus(String status){ this.status=status; }

    public Instant getCreatedAt(){  return createdAt;  }
    public void setCreatedAt(Instant createdAt){ this.createdAt=createdAt; }

    public Instant getUpdatedAt(){  return updatedAt;  }
    public void setUpdatedAt(Instant updatedAt){ this.updatedAt=updatedAt; }


}
