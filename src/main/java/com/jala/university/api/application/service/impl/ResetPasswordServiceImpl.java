package com.jala.university.api.application.service.impl;

import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.EmailService;
import com.jala.university.api.application.service.ResetPasswordService;
import com.jala.university.api.application.service.TokenService;
import com.jala.university.api.application.service.ValidationService;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import com.jala.university.api.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {
  private final UserRepository userRepository;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final ValidationService validationService;
  private final EmailService emailService;

  private final int expirationTimeTokenInHours = 1;
  private final UserMapper userMapper = new UserMapper();

  @Autowired
  public ResetPasswordServiceImpl(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder,
                                  ValidationService validationService, EmailService emailService) {
    this.emailService = emailService;
    this.userRepository = userRepository;
    this.tokenService = tokenService;
    this.passwordEncoder = passwordEncoder;
    this.validationService = validationService;
  }

  @Override
  public final boolean sendPasswordResetEmail(String email) throws MessagingException, InvalidEmailFormatException {
    if (!validationService.isValidEmail(email)) {
      throw new InvalidEmailFormatException(email);
    }
    var user = userRepository.findByLogin(email);
    if (user == null) {
      return false;
    }

    var token = tokenService.createToken(LocalDateTime.now().plusHours(expirationTimeTokenInHours), userMapper.mapTo(user));
    emailService.sendEmail(email, "Reset password", token.getToken().toString());
    return true;
  }

  @Override
  public final boolean resetPassword(UUID token, String password) throws InvalidPasswordFormatException {
    if (!validationService.isValidPassword(password)) {
      throw new InvalidPasswordFormatException();
    }
    if (!tokenService.verifyToken(token)) {
      return false;
    }

    var identityToken = tokenService.getToken(token);
    var user = identityToken.get().getUser();
    String encryptPassword = passwordEncoder.encode(password);
    user.setPassword(encryptPassword);
    userRepository.save(userMapper.mapFrom(user));
    return true;
  }
}
