package com.jala.university.api.application.service.impl;

import com.jala.university.api.application.dto.UserCredentials;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.AuthServices;
import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.entity.UserExt;
import com.jala.university.api.domain.exceptions.authentication.InvalidAuthenticationCredentialsException;
import com.jala.university.api.domain.exceptions.authentication.UserNotValidatedException;
import com.jala.university.api.domain.repository.UserExtRepository;
import com.jala.university.api.domain.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthServices {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final UserExtRepository userExtRepository;

  @Autowired
  public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, UserExtRepository userExtRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
    this.userExtRepository = userExtRepository;
  }

  /**
   * Authenticates a user with the given email and password.
   *
   * @param credentials user credentials.
   * @return a UserDto containing the authenticated user's details.
   * @throws InvalidAuthenticationCredentialsException if the email or password is incorrect.
   * @throws UserNotValidatedException if the user's account has not been validated.
   */
  @Override
  public UserDto login(UserCredentials credentials) throws InvalidAuthenticationCredentialsException, UserNotValidatedException {
    User userEntity = userRepository.findByLogin(credentials.getLogin());

    if (userEntity == null) {
      throw new InvalidAuthenticationCredentialsException();
    }

    if (!passwordEncoder.matches(credentials.getPassword(), userEntity.getPassword())
        && !credentials.getPassword().equals(userEntity.getPassword())) {
      throw new InvalidAuthenticationCredentialsException();
    }

    if (!isValidated(userEntity)) {
      throw new UserNotValidatedException();
    }

    return userMapper.mapTo(userEntity);
  }

  /**
   * Checks if a user's account has been validated.
   *
   * @param user the User entity to check for validation.
   * @return true if the user is validated, false otherwise.
   * If no UserExt is found for the user, it is assumed to be validated (returns true).
   */
  private boolean isValidated(User user) {
    Optional<UserExt> userExt = userExtRepository.findByUser(user);

    return userExt.map(UserExt::isValidated).orElse(true);
  }
}
