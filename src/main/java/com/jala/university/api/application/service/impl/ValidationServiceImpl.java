package com.jala.university.api.application.service.impl;

import com.jala.university.api.application.service.ValidationService;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService {
  private final int minChar = 8;
  private final int maxChar = 16;

  /**
   * Validates the email format using a regular expression.
   *
   * @param email the email to validate.
   * @return true if the email is valid, false otherwise.
   */
  @Override
  public boolean isValidEmail(String email) {
    String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$";
    Pattern pattern = Pattern.compile(emailPattern);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  /**
   * Validates the password based on length, digits, uppercase letters,
   * lowercase letters, and special characters.
   *
   * @param password the password to validate.
   * @return true if the password is valid, false otherwise.
   */
  @Override
  public boolean isValidPassword(String password) {
    return password.length() >= minChar && password.length() <= maxChar
        && password.matches(".*\\d.*")
        && password.matches(".*[A-Z].*")
        && password.matches(".*[a-z].*")
        && password.matches(".*[!@#$%^&*_].*");
  }

  /**
   * Validates the email and password.
   * If either one is invalid, it throws an IllegalArgumentException.
   *
   * @param email    The email to validate.
   * @param password The password to validate.
   * @throws InvalidEmailFormatException    If the email is invalid.
   * @throws InvalidPasswordFormatException If the password is invalid.
   */
  @Override
  public void isValidEmailAndPassword(String email, String password) throws InvalidEmailFormatException, InvalidPasswordFormatException {
    if (!isValidEmail(email)) {
      throw new InvalidEmailFormatException();
    }
    if (!isValidPassword(password)) {
      throw new InvalidPasswordFormatException();
    }
  }
}
