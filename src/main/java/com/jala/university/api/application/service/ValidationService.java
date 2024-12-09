package com.jala.university.api.application.service;

import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;

public interface ValidationService {
  boolean isValidEmail(String email);

  boolean isValidPassword(String password);

  void isValidEmailAndPassword(String email, String password) throws InvalidEmailFormatException, InvalidPasswordFormatException;
}
