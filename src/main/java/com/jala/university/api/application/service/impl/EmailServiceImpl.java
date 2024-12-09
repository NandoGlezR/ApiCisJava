package com.jala.university.api.application.service.impl;

import com.jala.university.api.application.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
  private JavaMailSender mailSender;

  @Autowired
  public EmailServiceImpl(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public final void sendEmail(String to, String subject, String body) throws MessagingException {
    MimeMessage mimeMessage  = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(body, true);

    mailSender.send(mimeMessage);
  }
}
