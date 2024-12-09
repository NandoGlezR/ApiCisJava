package com.jala.university.api.domain.exceptions.format;

import com.jala.university.api.domain.exceptions.FormatException;

/**
 * This exception is thrown when an email address is in an invalid format.
 */

public class InvalidEmailFormatException extends FormatException {
  public InvalidEmailFormatException(String message) {
    super(message);
  }

  public InvalidEmailFormatException() {
    super("Invalid email format");
  }
}
