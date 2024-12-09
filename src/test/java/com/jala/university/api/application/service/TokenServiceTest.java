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
import com.jala.university.api.application.factories.IdentityTokenFactory;
import com.jala.university.api.application.mapper.impl.IdentityValidationTokenMapper;
import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.impl.TokenServiceImpl;
import com.jala.university.api.domain.entity.IdentityValidationToken;
import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TokenServiceTest {
  @Mock
  static private UserRepository userRepository;
  @Mock
  static private IdentityTokenFactory identityTokenFactory;

  static private TokenService tokenService;
  static private User testUser;

  @BeforeEach
  void setUp() {
    try (var openMocks = MockitoAnnotations.openMocks(this)) {
      tokenService = new TokenServiceImpl(
          new IdentityValidationTokenMapper(), new UserMapper(),
          userRepository, identityTokenFactory);
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
    User user = User.builder()
        .login("example@example.com")
        .tokens(new ArrayList<>())
        .build();

    when(userRepository.findByLogin(any())).thenAnswer(invocationOnMock -> user);
    when(userRepository.save(any())).thenReturn(user);
    when(identityTokenFactory.create(any()))
        .thenAnswer(invocationOnMock -> {
          LocalDateTime expiration = invocationOnMock.getArgument(0);

          return IdentityValidationToken.builder()
              .id(tokenId)
              .expiration(expiration)
              .build();
        });

    assertDoesNotThrow(() -> {
      UserDto userDto = UserDto.builder().build();
      LocalDateTime expirationDate = LocalDateTime.now().plusHours(10);
      IdentityValidationTokenDto token = tokenService.createToken(expirationDate, userDto);

      assertNotNull(token);
      assertEquals(tokenId.toString(), token.getToken());
      assertEquals(expirationDate, token.getExpiration());
    });
  }

  @Test
  void VerifyExpiredTokenTest() {
    when(userRepository.findUserWithToken(any())).thenAnswer(invocationOnMock -> {
      UUID id = invocationOnMock.getArgument(0);
      var token = IdentityValidationToken.builder()
          .id(id)
          .expiration(LocalDateTime.now().minusHours(10))
          .build();
      var tokens = new ArrayList<IdentityValidationToken>();

      tokens.add(token);

      return Optional.of(User.builder()
          .tokens(tokens)
          .build());
    });

    assertFalse(tokenService.verifyToken(UUID.randomUUID()));
  }

  @Test
  void VerifyNonexistentTokenTest() {
    when(userRepository.findUserWithToken(any())).thenReturn(Optional.empty());

    assertFalse(tokenService.verifyToken(UUID.randomUUID()));
  }

  @Test
  void VerifyAlreadyUsedTokenTest() {
    when(userRepository.findUserWithToken(any())).thenAnswer(invocationOnMock -> {
      UUID id = invocationOnMock.getArgument(0);
      var token = IdentityValidationToken.builder()
          .id(id)
          .verified(true)
          .build();
      var tokens = new ArrayList<IdentityValidationToken>();

      tokens.add(token);

      return Optional.of(User.builder()
          .tokens(tokens)
          .build());
    });

    assertFalse(tokenService.verifyToken(UUID.randomUUID()));
  }

  @Test
  void VerifyValidTokenTest() {
    when(userRepository.findUserWithToken(any())).thenAnswer(invocationOnMock -> {
      UUID id = invocationOnMock.getArgument(0);
      var token = IdentityValidationToken.builder()
          .id(id)
          .expiration(LocalDateTime.now().plusHours(10))
          .verified(false)
          .build();
      var tokens = new ArrayList<IdentityValidationToken>();

      tokens.add(token);

      return Optional.of(User.builder().tokens(tokens).build());
    });

    assertTrue(tokenService.verifyToken(UUID.randomUUID()));
  }

  @Test
  void GetExistingTokenTest() {
    when(userRepository.findUserWithToken(any())).thenAnswer(invocationOnMock -> {
      UUID id = invocationOnMock.getArgument(0);
      var token = IdentityValidationToken.builder()
          .id(id)
          .build();
      var tokens = new ArrayList<IdentityValidationToken>();

      tokens.add(token);

      return Optional.of(User.builder()
          .tokens(tokens)
          .build());
    });

    UUID id = UUID.randomUUID();

    Optional<UserDto> optionalUser = tokenService.getUserWithToken(id);

    assertTrue(optionalUser.isPresent());
  }

  @Test
  void GetNonexistentTokenTest() {
    when(userRepository.findUserWithToken(any())).thenReturn(Optional.empty());

    assertTrue(tokenService.getUserWithToken(UUID.randomUUID()).isEmpty());
  }
}
