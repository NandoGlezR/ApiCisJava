package com.jala.university.api.infrastructure.persistence.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    private static final int MILLISECONDS_IN_A_SECOND = 1000;

    /**
     * Generates a JWT token for the given user ID.
     *
     * @param userId the user ID
     * @return the generated JWT token
     */
    public String generateToken(String userId) {
        Key key = JwtKeyUtil.getKeyFromSecret(secretKey);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * MILLISECONDS_IN_A_SECOND))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

  /**
   * Validates the given JWT token for the specified user ID.
   *
   * @param token the JWT token
   * @param userId the user ID
   * @param jwtTokenUtil the JWT token utility
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token, String userId, JwtTokenUtil jwtTokenUtil) {
    final String tokenUserId = jwtTokenUtil.getUserIdFromToken(token);
    return (tokenUserId.equals(userId) && !jwtTokenUtil.isTokenExpired(token));
  }
}

