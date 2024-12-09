package com.jala.university.api.application.service;

import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import com.jala.university.api.domain.exceptions.user.UserAlreadyRegisteredException;
import com.jala.university.api.domain.exceptions.user.UserNotFoundException;
import jakarta.mail.MessagingException;
import java.util.UUID;

public interface UserService {

    UserDto getUserById(String id) throws UserNotFoundException;

    UserDto createUser(UserDto user)
            throws InvalidPasswordFormatException, InvalidEmailFormatException, UserAlreadyRegisteredException, MessagingException;

    UserDto updateUser(String id, UserDto user)
            throws UserNotFoundException, InvalidEmailFormatException, InvalidPasswordFormatException, MessagingException;

    String deleteUser(String id) throws UserNotFoundException;

    boolean validateUserEmail(UUID token);

}
