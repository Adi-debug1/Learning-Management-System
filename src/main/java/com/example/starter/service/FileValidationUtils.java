package com.example.starter.service;

public class FileValidationUtils {

  public static boolean isValidFileType(String fileName){
    String ext = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
    return ext.equals("pdf") || ext.equals("jpg") || ext.equals("png");
  }

  public static boolean isValidFileSize(long sizeInBytes){
    return sizeInBytes <= 5*1024*1024;// 5mb
  }
}
