package com.jala.university.api.domain.exceptions.format;

import com.jala.university.api.domain.exceptions.FormatException;

/**
 * This exception is thrown when a password is in an invalid format.
 * This typically indicates that the password does not meet certain criteria, such as minimum length,
 * complexity requirements, or special character usage.
 */

public class InvalidPasswordFormatException extends FormatException {
  public InvalidPasswordFormatException() {
    super("Invalid Password");
  }

  public InvalidPasswordFormatException(String message) {
    super(message);
  }
}
