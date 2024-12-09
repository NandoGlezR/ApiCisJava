package com.jala.university.api.infrastructure.persistence.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;
  private JwtTokenUtil jwtTokenUtil;
  private String testUserId = "1";
  private String secretKey = "NcA1xBfJrjGyryOOA8axupfFY7Mn6jhFMESDkl9BCaTJAak0MwCUJ5Lp8wzz4/ISzDvgSGhr6ANxS0velah4AA==";  // Example secretKey
  private Key key;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = new JwtTokenProvider();
    jwtTokenUtil = new JwtTokenUtil();

    ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", secretKey);
    ReflectionTestUtils.setField(jwtTokenUtil, "secretKey", secretKey);

    key = JwtKeyUtil.getKeyFromSecret(secretKey);
  }

  @Test
  void testGenerateToken() {
    String token = jwtTokenProvider.generateToken(testUserId);

    assertNotNull(token);
    String userIdFromToken = jwtTokenUtil.getUserIdFromToken(token);
    assertEquals(testUserId, userIdFromToken);
  }

  @Test
  void testValidateToken_Success() {
    String token = jwtTokenProvider.generateToken(testUserId);

    boolean isValid = jwtTokenProvider.validateToken(token, testUserId, jwtTokenUtil);

    assertTrue(isValid);
  }

  @Test
  void testValidateTokenFailureInvalidUserId() {
    String token = jwtTokenProvider.generateToken(testUserId);
    String invalidUserId = "invalidUser";

    boolean isValid = jwtTokenProvider.validateToken(token, invalidUserId, jwtTokenUtil);

    assertFalse(isValid);
  }

  @Test
  void testValidateTokenFailureExpiredToken() {
    // Arrange
    String expiredToken = Jwts.builder()
    .setSubject(testUserId)
    .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
    .setExpiration(new Date(System.currentTimeMillis() - 5000))
    .signWith(key, SignatureAlgorithm.HS512)
    .compact();

    Exception exception = assertThrows(ExpiredJwtException.class, () -> {
      jwtTokenProvider.validateToken(expiredToken, testUserId, jwtTokenUtil);
    });

    assertTrue(exception.getMessage().contains("JWT expired"));
  }
}
