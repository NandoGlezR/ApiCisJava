package com.jala.university.api.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.jala.university.api.infrastructure.persistence.utils.CreateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@Data
@Schema(name = "User")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
  @JsonProperty(access = Access.READ_ONLY)
  private String id;

  @NotEmpty(message = "name must not be empty", groups = CreateGroup.class)
  private String name;

  @NotEmpty(message = "email must not be empty", groups = CreateGroup.class)
  private String email;

  @JsonProperty(access = Access.WRITE_ONLY)
  @NotEmpty(message = "password must not be empty", groups = CreateGroup.class)
  private String password;

  @JsonProperty(access = Access.READ_ONLY)
  private boolean validated;
}
