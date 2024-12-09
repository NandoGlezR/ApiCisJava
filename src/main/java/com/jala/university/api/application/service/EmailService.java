package com.jala.university.api.application.service;

import jakarta.mail.MessagingException;

public interface EmailService {
  /**
   * Sends an email that could have HTML embedded.
   *
   * @param to recipient of the email.
   * @param subject subject of the email.
   * @param body body of the email (could contain HTML).
   */
  void sendEmail(String to, String subject, String body) throws MessagingException;
}
