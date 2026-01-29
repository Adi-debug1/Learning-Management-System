package com.example.starter.service;

import com.example.starter.enums.Role;
import com.example.starter.enums.UploadStatus;
import com.example.starter.enums.UploadType;
import com.example.starter.model.BulkUpload;
import com.example.starter.model.Student;
import com.example.starter.model.Teacher;
import com.example.starter.repository.BulkUploadRepository;
import com.example.starter.repository.StudentRepository;
import com.example.starter.repository.TeacherRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class BulkUploadProcessor {

  private final StudentRepository studentRepository = new StudentRepository();
  private final TeacherRepository teacherRepository = new TeacherRepository();
  private final BulkUploadRepository bulkUploadRepository = new BulkUploadRepository();

  public void process(String filePath, BulkUpload bulkUpload){
    int total = 0;
    int success = 0;
    int failure = 0;

    boolean hasStudent = false;
    boolean hasTeacher = false;

    Set<String> emailSet = new HashSet<>();

    try(BufferedReader br = new BufferedReader(new FileReader(filePath))){

      String line;
      boolean headerSkipped = false;

      while((line = br.readLine()) != null){
        if(!headerSkipped){
          headerSkipped = true;
          continue;
        }

        if(line.trim().isEmpty()) continue;

        total++;

        String[] data = line.split(",");

        if(data.length != 5){
          failure++;
          continue;
        }

        String fullName = data[0].trim();
        String email = data[1].trim();
        String mobile = data[2].trim();
        String roleStr = data[3].trim();
        String password = data[4].trim();

        // duplicate in same file
        if(!emailSet.add(email)){
          failure++;
          continue;
        }

        // mobile validation length check
        if(mobile.length() != 10){
          failure++;
          continue;
        }

        Role role;
        try{
          role = Role.valueOf(roleStr);
        }catch (Exception e){
          failure++;
          continue;
        }

        if(role == Role.STUDENT){
          hasStudent = true;

          // CORRECT LOGIC
          if(studentRepository.findByEmail(email) != null){
            failure++;
            continue;
          }
          String hashPassword = BCrypt.hashpw(
            password,
            BCrypt.gensalt(10)
          );

          Student student = new Student();
          student.setFullName(fullName);
          student.setEmail(email);
          student.setMobileNumber(mobile);
          student.setPassword(hashPassword);
          student.setCreatedAt(Instant.now());
          student.setStatus("Active");
          student.setRole(Role.STUDENT);

          studentRepository.save(student);
          success++;

        } else if(role == Role.TEACHER){
          hasTeacher = true;

          if(teacherRepository.findByEmail(email) != null){
            failure++;
            continue;
          }
          String hashPassword = BCrypt.hashpw(
            password,
            BCrypt.gensalt(10)
          );

          Teacher teacher = new Teacher();
          teacher.setFullName(fullName);
          teacher.setEmail(email);
          teacher.setMobileNumber(mobile);
          teacher.setPassword(hashPassword);
          teacher.setCreatedAt(Instant.now());
          teacher.setStatus("Active");
          teacher.setRole(Role.TEACHER);

          teacherRepository.save(teacher);
          success++;

        } else {
          failure++;
        }
      }

      // upload type
      if(hasStudent && hasTeacher){
        bulkUpload.setUploadType(UploadType.MIXED);
      } else if(hasStudent){
        bulkUpload.setUploadType(UploadType.STUDENT);
      } else{
        bulkUpload.setUploadType(UploadType.TEACHER);
      }

      bulkUpload.setTotalRecords(total);
      bulkUpload.setSuccessCount(success);
      bulkUpload.setFailureCount(failure);
      bulkUpload.setStatus(UploadStatus.COMPLETED);
      bulkUpload.setCompletedAt(Instant.now());

      bulkUploadRepository.update(bulkUpload);

    } catch (Exception e) {
      bulkUpload.setStatus(UploadStatus.FAILED);
      bulkUploadRepository.update(bulkUpload);
    }

    System.out.println("Success: " + success);
    System.out.println("Failure: " + failure);
  }
}
