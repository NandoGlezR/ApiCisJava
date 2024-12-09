package com.jala.university.api.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.jala.university.api.application.dto.IdentityValidationTokenDto;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.mapper.impl.IdentityValidationTokenMapper;
import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.impl.TokenServiceImpl;
import com.jala.university.api.domain.entity.IdentityValidationToken;
import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.repository.IdentityValidationTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TokenServiceTest {
  @Mock
  static private IdentityValidationTokenRepository identityValidationTokenRepository;

  static private TokenService tokenService;

  @BeforeEach
  void setUp() {
    try (var openMocks = MockitoAnnotations.openMocks(this)) {
      tokenService = new TokenServiceImpl(new IdentityValidationTokenMapper(), new UserMapper(), identityValidationTokenRepository);
    } catch (Exception ignored) {}
  }

  @Test
  void CreateTokenWithInvalidExpirationDateTest() {
    assertThrows(IllegalArgumentException.class, () -> {
      tokenService.createToken(LocalDateTime.now(), UserDto.builder().build());
    });

    assertThrows(IllegalArgumentException.class, () -> {
      tokenService.createToken(LocalDateTime.now().minusDays(1), UserDto.builder().build());
    });
  }

  @Test
  void CreateTokenWithValidExpirationDateTest() {
    UUID tokenId = UUID.randomUUID();

    when(identityValidationTokenRepository.save(any())).thenAnswer(invocationOnMock -> {
      IdentityValidationToken token = invocationOnMock.getArgument(0);

      token.setId(tokenId);

      return token;
    });

    assertDoesNotThrow(() -> {
      UserDto userDto = UserDto.builder().build();
      LocalDateTime expirationDate = LocalDateTime.now().plusHours(10);
      IdentityValidationTokenDto token = tokenService.createToken(expirationDate, userDto);

      assertNotNull(token);
      assertEquals(tokenId, token.getToken());
      assertEquals(userDto, token.getUser());
      assertEquals(expirationDate, token.getExpiration());
    });
  }

  @Test
  void VerifyExpiredTokenTest() {
    when(identityValidationTokenRepository.findById(any())).thenAnswer(invocationOnMock -> {
      UUID id = invocationOnMock.getArgument(0);

      return Optional.of(IdentityValidationToken.builder()
          .id(id)
          .expiration(LocalDateTime.now().minusHours(10))
          .build());
    });

    assertFalse(tokenService.verifyToken(UUID.randomUUID()));
  }

  @Test
  void VerifyNonexistentTokenTest() {
    when(identityValidationTokenRepository.findById(any())).thenReturn(Optional.empty());

    assertFalse(tokenService.verifyToken(UUID.randomUUID()));
  }

  @Test
  void VerifyAlreadyUsedTokenTest() {
    when(identityValidationTokenRepository.findById(any()))
        .thenReturn(Optional.of(IdentityValidationToken.builder()
            .verified(true)
            .build()));

    assertFalse(tokenService.verifyToken(UUID.randomUUID()));
  }

  @Test
  void VerifyValidTokenTest() {
    when(identityValidationTokenRepository.findById(any()))
        .thenReturn(Optional.of(IdentityValidationToken.builder()
            .verified(false)
            .expiration(LocalDateTime.now().plusDays(10))
            .build()));

    assertTrue(tokenService.verifyToken(UUID.randomUUID()));
  }

  @Test
  void GetExistingTokenTest() {
    when(identityValidationTokenRepository.findById(any())).thenAnswer(invocationOnMock -> {
      UUID id = invocationOnMock.getArgument(0);

      return Optional.of(IdentityValidationToken.builder().id(id).user(new User()).build());
    });

    UUID id = UUID.randomUUID();

    Optional<IdentityValidationTokenDto> optionalToken = tokenService.getToken(id);

    assertTrue(optionalToken.isPresent());
    assertEquals(id, optionalToken.get().getToken());
  }

  @Test
  void GetNonexistentTokenTest() {
    when(identityValidationTokenRepository.findById(any())).thenReturn(Optional.empty());

    assertTrue(tokenService.getToken(UUID.randomUUID()).isEmpty());
  }
}
