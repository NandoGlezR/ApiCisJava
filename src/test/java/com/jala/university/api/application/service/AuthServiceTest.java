package com.jala.university.api.application.service;

import com.jala.university.api.application.dto.UserCredentials;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.impl.AuthServiceImpl;
import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.exceptions.authentication.InvalidAuthenticationCredentialsException;
import com.jala.university.api.domain.exceptions.authentication.UserNotValidatedException;
import com.jala.university.api.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private AuthServiceImpl authService;

  private User user;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    user = User.builder().id(UUID.randomUUID().toString())
        .name("test").login("test@test.com").password("password").build();
  }

  @Test
  void testLoginInvalidCredentials() {
    UserCredentials credentials = UserCredentials.builder()
        .login("non-existent-email@example.com")
        .password("password")
        .build();

    when(userRepository.findByLogin(user.getLogin())).thenReturn(null);
    assertThrows(InvalidAuthenticationCredentialsException.class, () -> authService.login(credentials));
  }

  @Test
  void testUserFondWithEmail()
          throws InvalidAuthenticationCredentialsException, UserNotValidatedException {
    UserCredentials credentials = UserCredentials.builder()
        .login(user.getLogin())
        .password(user.getPassword())
        .build();

    when(userRepository.findByLogin(user.getLogin())).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    user.setValidated(true);
    when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);
    when(userMapper.mapTo(user))
        .thenReturn(UserDto.builder().id(user.getId())
            .name(user.getName()).email(user.getLogin())
            .password(user.getPassword()).build());

    UserDto userDto = authService.login(credentials);
    assertNotNull(userDto);
  }

  @Test
  void testPasswordFondWithEmail()
          throws InvalidAuthenticationCredentialsException, UserNotValidatedException {
    UserCredentials credentials = UserCredentials.builder()
        .login(user.getLogin())
        .password(user.getPassword())
        .build();

    when(userRepository.findByLogin(user.getLogin())).thenReturn(user);
    user.setValidated(true);
    when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(userMapper.mapTo(user))
        .thenReturn(UserDto.builder().id(user.getId())
            .name(user.getName()).email(user.getLogin())
            .password(user.getPassword()).build());

    UserDto userDto = authService.login(credentials);
    assertNotNull(userDto);
  }

  @Test
  void testPasswordNotFondWithEmail() {
    UserCredentials credentials = UserCredentials.builder()
        .login(user.getLogin())
        .password("badPassword")
        .build();

    when(userRepository.findByLogin(user.getLogin())).thenReturn(user);
    when(passwordEncoder.matches(user.getPassword(), credentials.getPassword())).thenReturn(false);

    assertThrows(InvalidAuthenticationCredentialsException.class, () -> authService.login(credentials));
  }

  @Test
  void testUserNotValidated() throws UserNotValidatedException {
    UserCredentials credentials = UserCredentials.builder()
        .login(user.getLogin())
        .password(user.getPassword())
        .build();

    when(userRepository.findByLogin(user.getLogin())).thenReturn(user);
    when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    assertThrows(UserNotValidatedException.class, () -> authService.login(credentials));
  }

  @Test
  void testUserValidated() throws InvalidAuthenticationCredentialsException, UserNotValidatedException {
    UserCredentials credentials = UserCredentials.builder()
        .login(user.getLogin())
        .password(user.getPassword())
        .build();

    when(userRepository.findByLogin(user.getLogin())).thenReturn(user);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);
    user.setValidated(true);
    when(userRepository.findById(String.valueOf(user))).thenReturn(Optional.ofNullable(user));

    when(userMapper.mapTo(user))
            .thenReturn(UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getLogin())
                    .password(user.getPassword()).build());

    UserDto userDto = authService.login(credentials);

    assertNotNull(userDto);
    assertEquals(user.getLogin(), userDto.getEmail());
  }

}