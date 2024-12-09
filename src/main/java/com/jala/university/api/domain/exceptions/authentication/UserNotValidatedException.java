package com.jala.university.api.domain.exceptions.authentication;

import com.jala.university.api.domain.exceptions.AuthenticationException;

public class UserNotValidatedException extends AuthenticationException {

    public UserNotValidatedException(String message) {

        super(message);
    }

    public UserNotValidatedException() {

        super("User not validated");
    }

}
