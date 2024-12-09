package com.jala.university.api.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jala.university.api.application.service.impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailServiceTest {
  @Mock
  static private JavaMailSender mailSender;

  static private EmailService emailService;
  static private String to;
  static private String subject;

  @BeforeEach
  void setUp() {
    try (var op = MockitoAnnotations.openMocks(this)) {
      emailService = new EmailServiceImpl(mailSender);

      when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
    } catch (Exception ignored) {}

    to = "test@exmaple.com";
    subject = "test";
  }

  @Test
  public void sendValidEmailTest() {
    String body = "<h1>Title</h1>";

    assertDoesNotThrow(() -> {
      emailService.sendEmail(to, subject, body);
    });
  }

  @Test
  public void sendPlainTextEmailTest() {
    String body = "plain text";

    assertDoesNotThrow(() -> {
      emailService.sendEmail(to, subject, body);
    });
  }

  @Test
  public void sendEmailToInvalidAddressTest() {
    String body = "<h1>Title</h1>";
    to = "invalid email";

    assertThrows(MessagingException.class, () -> {
      emailService.sendEmail(to, subject, body);
    });
  }
}
