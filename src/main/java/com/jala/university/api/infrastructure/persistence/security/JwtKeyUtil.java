package com.jala.university.api.infrastructure.persistence.security;

import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;

public class JwtKeyUtil {

  public static final int SPLIT_INDEX = 7;

  /**
   * Converts the Base64 encoded secret key into a {@link Key} object.
   *
   * @param secretKey The secret key in Base64 format.
   * @return The converted secret key.
   */
  public static Key getKeyFromSecret(String secretKey) {
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
