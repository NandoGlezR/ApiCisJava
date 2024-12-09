package com.jala.university.api.controller;

import com.jala.university.api.application.service.ResetPasswordService;
import com.jala.university.api.application.service.ValidationService;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PasswordResetControllerTest {

  @Mock
  private ResetPasswordService resetPasswordService;

  @InjectMocks
  private PasswordResetController passwordResetController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testRequestPasswordResetSuccess() throws MessagingException, InvalidEmailFormatException {
    // Arrange
    String email = "test@example.com";
    when(resetPasswordService.sendPasswordResetEmail(email)).thenReturn(true);

    // Act
    ResponseEntity<String> response = passwordResetController.requestPasswordReset(email);

    // Assert
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals("If the email exists, a password recovery link has been sent.", response.getBody());
    verify(resetPasswordService, times(1)).sendPasswordResetEmail(email);
  }

  @Test
  void testRequestPasswordResetEmailNotFound() throws MessagingException, InvalidEmailFormatException {
    // Arrange
    String email = "invalid@example.com";
    when(resetPasswordService.sendPasswordResetEmail(email)).thenReturn(false);

    // Act
    ResponseEntity<String> response = passwordResetController.requestPasswordReset(email);

    // Assert
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals("If the email exists, a password recovery link has been sent.", response.getBody());
    verify(resetPasswordService, times(1)).sendPasswordResetEmail(email);
  }

  @Test
  void testRequestPasswordResetMessagingException() throws MessagingException, InvalidEmailFormatException {
    // Arrange
    String email = "test@example.com";
    when(resetPasswordService.sendPasswordResetEmail(email)).thenThrow(new MessagingException("Email sending failed"));

    // Act
    ResponseEntity<String> response = passwordResetController.requestPasswordReset(email);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred while processing your request.", response.getBody());
    verify(resetPasswordService, times(1)).sendPasswordResetEmail(email);
  }

  @Test
  void testUpdatePasswordSuccess() throws InvalidPasswordFormatException {
    // Arrange
    UUID token = UUID.randomUUID();
    String password = "newPassword";
    when(resetPasswordService.resetPassword(eq(token), eq(password))).thenReturn(true);

    // Act
    ResponseEntity<String> response = passwordResetController.updatePassword(token, password);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Password has been updated.", response.getBody());
    verify(resetPasswordService, times(1)).resetPassword(eq(token), eq(password));
  }

  @Test
  void testUpdatePasswordFailure() throws InvalidPasswordFormatException {
    // Arrange
    UUID token = UUID.randomUUID();
    String password = "newPassword";
    when(resetPasswordService.resetPassword(eq(token), eq(password))).thenReturn(false);

    // Act
    ResponseEntity<String> response = passwordResetController.updatePassword(token, password);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid or expired token", response.getBody());
    verify(resetPasswordService, times(1)).resetPassword(eq(token), eq(password));
  }

  @Test
  void testUpdatePasswordMessaginException() throws InvalidPasswordFormatException {
    UUID token = UUID.randomUUID();
    String password = "newPassword";
    doThrow(new InvalidPasswordFormatException()).when(resetPasswordService).resetPassword(token, password);
    ResponseEntity<String> response = passwordResetController.updatePassword(token, password);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid password format.", response.getBody());
  }

  @Test
  void testInvalidEmailFormatException() throws MessagingException, InvalidEmailFormatException {
    String email = "invalidexample.com";
    doThrow(new InvalidEmailFormatException()).when(resetPasswordService).sendPasswordResetEmail(email);
    ResponseEntity<String> response = passwordResetController.requestPasswordReset(email);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid email format.", response.getBody());
  }
}
