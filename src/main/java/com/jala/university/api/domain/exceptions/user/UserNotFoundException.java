package com.jala.university.api.domain.exceptions.user;

import com.jala.university.api.domain.exceptions.UserException;

/**
 * This exception is thrown when a user cannot be found in the DB.
 * This typically indicates that the specified user ID or username does not exist.
 */
public class UserNotFoundException extends UserException {

  public UserNotFoundException(String message) {

    super(message);
  }

  public UserNotFoundException() {

    super("User not found");
  }
}
