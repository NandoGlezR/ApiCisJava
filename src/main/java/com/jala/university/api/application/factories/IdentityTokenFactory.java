package com.jala.university.api.application.factories;

import com.jala.university.api.domain.entity.IdentityValidationToken;
import com.jala.university.api.domain.entity.User;
import java.time.LocalDateTime;

public class IdentityTokenFactory {
  public final IdentityValidationToken create(LocalDateTime expiration, User user) {
    return IdentityValidationToken.builder().expiration(expiration).user(user).build();
  }
}
