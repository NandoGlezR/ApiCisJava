package com.jala.university.api.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@NonNull
@EqualsAndHashCode
@Data
@Schema(name = "User credentials")
public class UserCredentials {
  @Schema(description = "email or username")
  @NotEmpty(message = "email or username mut not be null")
  private String login;
  @NotEmpty(message = "password must not be null")
  private String password;
}
