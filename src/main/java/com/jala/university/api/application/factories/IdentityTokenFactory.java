package com.jala.university.api.application.factories;

import com.jala.university.api.domain.entity.IdentityValidationToken;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class IdentityTokenFactory {
  public final IdentityValidationToken create(LocalDateTime expiration) {
    return IdentityValidationToken.builder()
        .id(UUID.randomUUID())
        .expiration(expiration)
        .build();
  }
}
