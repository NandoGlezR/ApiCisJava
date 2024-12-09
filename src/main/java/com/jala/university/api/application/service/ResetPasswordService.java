package com.jala.university.api.application.service;

import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import jakarta.mail.MessagingException;

import java.util.UUID;

public interface ResetPasswordService {

  /**
   * Sends an email to the specified user with instructions to reset their password.
   *
   * @param email the email address of the user requesting the password reset.
   * @return {@code true} if the email was successfully sent, {@code false} otherwise.
   * @throws MessagingException if there is an issue while sending the email.
   */
  boolean sendPasswordResetEmail(String email) throws MessagingException, InvalidEmailFormatException;

  /**
   * Resets the user's password using the provided token and new password.
   *
   * @param token    the unique token for password reset verification.
   * @param password the new password to set for the user.
   * @return {@code true} if the password was successfully reset, {@code false} otherwise.
   */
  boolean resetPassword(UUID token, String password) throws InvalidPasswordFormatException;
}
