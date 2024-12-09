package com.jala.university.api.domain.exceptions;

/**
 * This exception is thrown when an authentication error occurs.
 * This can include various issues related to user authentication, such as invalid credentials.
 */
public class AuthenticationException extends Exception {
  public AuthenticationException(String message) {
    super(message);
  }
}
