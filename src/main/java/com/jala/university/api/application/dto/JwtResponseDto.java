package com.jala.university.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used to encapsulate the JWT access token in the response.
 * This class contains the access token that is generated upon successful authentication
 * and is sent to the client as part of the authentication response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {

  /**
   * The JWT access token that allows the client to access secured resources.
   */
  private String accessToken;
}
