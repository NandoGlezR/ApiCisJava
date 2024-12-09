package com.jala.university.api.domain.exceptions;

/**
 * This exception is thrown when a formatting error occurs.
 * This can include various issues related to invalid data formats, invalid numerical values, or malformed strings.
 */
public class FormatException extends Exception {
  public FormatException(String message) {
    super(message);
  }
}
