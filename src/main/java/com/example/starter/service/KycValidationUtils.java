package com.example.starter.service;

public class KycValidationUtils {

  public static boolean isValidPan(String pan) {
    return pan.matches("[A-Z]{5}[0-9]{4}[A-Z]");
  }

  public static boolean isValidAadhaar(String aadhaar) {
    return aadhaar.matches("\\d{12}");
  }

  public static boolean isValidPassport(String passport) {
    return passport.matches("[A-Z]{1}[0-9]{7}");
  }

  public static boolean isNameMatching(String userName, String docName) {
    return userName.equalsIgnoreCase(docName);
  }
}
