package com.jala.university.api.application.service;

import com.jala.university.api.application.dto.UserCredentials;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.domain.exceptions.authentication.InvalidAuthenticationCredentialsException;
import com.jala.university.api.domain.exceptions.authentication.UserNotValidatedException;

public interface AuthServices {
  UserDto login(UserCredentials credentials) throws InvalidAuthenticationCredentialsException, UserNotValidatedException;
}
