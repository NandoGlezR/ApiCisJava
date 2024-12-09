package com.jala.university.api.domain.exceptions;

/**
 * This exception is thrown when an error occurs related to a user.
 */
public class UserException extends Exception {
  public UserException(String message) {
    super(message);
  }
}

