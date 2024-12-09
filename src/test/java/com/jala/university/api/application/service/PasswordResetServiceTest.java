package com.jala.university.api.application.service;

import com.jala.university.api.application.dto.IdentityValidationTokenDto;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.impl.ResetPasswordServiceImpl;
import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import com.jala.university.api.domain.exceptions.user.UserNotFoundException;
import com.jala.university.api.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PasswordResetServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private TokenService tokenService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private ValidationService validationService;

  @Mock
  private EmailService emailService;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private ResetPasswordServiceImpl resetPasswordServicesImpl;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSendPasswordResetEmail_InvalidEmail() throws MessagingException{
    String invalidEmail = "invalid-email";

    when(validationService.isValidEmail(invalidEmail)).thenReturn(false);

    assertThrows(InvalidEmailFormatException.class, () -> resetPasswordServicesImpl.sendPasswordResetEmail(invalidEmail));
    verify(validationService).isValidEmail(invalidEmail);
    verify(userRepository, never()).findByLogin(anyString());
  }

  @Test
  public void testSendPasswordResetEmail_UserNotFound()
      throws MessagingException, InvalidEmailFormatException, UserNotFoundException {
    String validEmail = "user@example.com";

    when(validationService.isValidEmail(validEmail)).thenReturn(true);
    when(userRepository.findByLogin(validEmail)).thenReturn(null);
    when(tokenService.createToken(any(), any())).thenThrow(new UserNotFoundException());

    boolean result = resetPasswordServicesImpl.sendPasswordResetEmail(validEmail);

    assertFalse(result);
    verify(tokenService).createToken(any(), any());
  }

  @Test
  public void testSendPasswordResetEmail_Success()
      throws MessagingException, InvalidEmailFormatException, UserNotFoundException {
    String validEmail = "user@example.com";
    var user = mock(User.class); // Mock del UserDto
    var token = mock(IdentityValidationTokenDto.class); // Mock del token

    when(validationService.isValidEmail(validEmail)).thenReturn(true);
    when(userRepository.findByLogin(validEmail)).thenReturn(user);
    when(tokenService.createToken(any(LocalDateTime.class), any(UserDto.class))).thenReturn(token);
    when(token.getToken()).thenReturn(String.valueOf(UUID.randomUUID()));

    boolean result = resetPasswordServicesImpl.sendPasswordResetEmail(validEmail);

    assertTrue(result);
    verify(emailService).sendEmail(eq(validEmail), eq("Reset password"), anyString());
  }

  @Test
  public void testResetPassword_InvalidToken() throws InvalidPasswordFormatException {
    UUID invalidToken = UUID.randomUUID();
    String newPassword = "n3w_Password";

    when(tokenService.verifyToken(invalidToken)).thenReturn(false);
    when(validationService.isValidPassword(newPassword)).thenReturn(true);
    boolean result = resetPasswordServicesImpl.resetPassword(invalidToken, newPassword);

    assertFalse(result);
    verify(tokenService).verifyToken(invalidToken);
    verify(userRepository, never()).save(any());
  }

  @Test
  public void testResetPassword_InvalidPassword() {
    UUID validToken = UUID.randomUUID();
    String newPassword = "new_Password";

    when(validationService.isValidPassword(newPassword)).thenReturn(false);
    assertThrows(InvalidPasswordFormatException.class, () -> resetPasswordServicesImpl.resetPassword(validToken, newPassword));
  }

  @Test
  public void testResetPassword_Success() throws InvalidPasswordFormatException {
    UUID validToken = UUID.randomUUID();
    String newPassword = "n3w_Password";
    var user = mock(UserDto.class);

    when(tokenService.verifyToken(validToken)).thenReturn(true);
    when(tokenService.getUserWithToken(validToken)).thenReturn(Optional.of(user));
    when(validationService.isValidPassword(newPassword)).thenReturn(true);
    when(passwordEncoder.encode(newPassword)).thenReturn("encryptedPassword");

    boolean result = resetPasswordServicesImpl.resetPassword(validToken, newPassword);

    assertTrue(result);
    verify(user).setPassword("encryptedPassword");
  }

}
