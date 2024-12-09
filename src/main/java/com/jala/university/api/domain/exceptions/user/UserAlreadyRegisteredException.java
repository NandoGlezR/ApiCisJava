package com.jala.university.api.domain.exceptions.user;

import com.jala.university.api.domain.exceptions.UserException;

public class UserAlreadyRegisteredException extends UserException {

  public UserAlreadyRegisteredException(String message) {
    super(message);
  }
}
