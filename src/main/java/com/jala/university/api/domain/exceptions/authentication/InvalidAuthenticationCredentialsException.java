package com.jala.university.api.domain.exceptions.authentication;

import com.jala.university.api.domain.exceptions.AuthenticationException;

/**
 * This exception is thrown when authentication credentials are invalid.
 * This typically indicates that the provided password or username is incorrect.
 */
public class InvalidAuthenticationCredentialsException extends AuthenticationException {
  public InvalidAuthenticationCredentialsException(String message) {
    super(message);
  }

  public InvalidAuthenticationCredentialsException() {
    super("Incorrect password or username");
  }
}
