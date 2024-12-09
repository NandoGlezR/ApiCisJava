package com.jala.university.api.application.service;

import com.jala.university.api.application.dto.IdentityValidationTokenDto;
import com.jala.university.api.application.dto.UserDto;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TokenService {

  /**
   * Creates a new token.
   *
   * @param expiration when the token will expire.
   * @param user owner of the token.
   * @return a new IdentityValidationTokenDto.
   */
  IdentityValidationTokenDto createToken(LocalDateTime expiration, UserDto user) throws InvalidParameterException;

  /**
   * Verifies the received token.
   * <p>
   * Verifies mean that we want to validate the identity of the user using this token.
   * Once a token has been verified it could not be used again.
   *
   * @param token token to be verified.
   * @return true if the token exists, haven't expired and haven't been verified already. False otherwise.
   */
  boolean verifyToken(UUID token);

  /**
   * Search for the IdentityValidationToken that corresponds to the received UUID.
   *
   * @param token token to be searched
   * @return an optional of IdentityValidationToken
   */
  Optional<IdentityValidationTokenDto> getToken(UUID token);
}
