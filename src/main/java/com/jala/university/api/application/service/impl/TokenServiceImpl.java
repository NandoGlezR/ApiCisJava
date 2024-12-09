package com.jala.university.api.application.service.impl;

import com.jala.university.api.application.dto.IdentityValidationTokenDto;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.factories.IdentityTokenFactory;
import com.jala.university.api.application.mapper.impl.IdentityValidationTokenMapper;
import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.TokenService;
import com.jala.university.api.domain.entity.IdentityValidationToken;
import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.exceptions.user.UserNotFoundException;
import com.jala.university.api.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private IdentityValidationTokenMapper mapper;
    private UserMapper userMapper;
    private UserRepository repository;
    private IdentityTokenFactory tokenFactory;

    @Autowired
    public TokenServiceImpl(IdentityValidationTokenMapper mapper,
                            UserMapper userMapper,
                            UserRepository repository,
                            IdentityTokenFactory tokenFactory) {
        this.mapper = mapper;
        this.userMapper = userMapper;
        this.repository = repository;
        this.tokenFactory = tokenFactory;
    }

    @Override
    public final IdentityValidationTokenDto createToken(LocalDateTime expiration, UserDto user)
        throws InvalidParameterException, UserNotFoundException {
        if (expiration.isBefore(LocalDateTime.now()) || expiration.isEqual(LocalDateTime.now())) {
            throw new InvalidParameterException("Expiration time must be after now");
        }

        Optional<User> optionalUser = Optional.ofNullable(repository.findByLogin(user.getEmail()));

        if (optionalUser.isEmpty()) {
          throw new UserNotFoundException("User not found");
        }

        User userOnDb = optionalUser.get();

        IdentityValidationToken token = tokenFactory.create(expiration);

        userOnDb.getTokens().add(token);
        repository.save(userOnDb);

        return mapper.mapTo(token);
    }

    @Override
    public final boolean verifyToken(UUID token) {

      Optional<User> userOptional = repository.findUserWithToken(token);

        if (userOptional.isEmpty()) {
            return false;
        }

        IdentityValidationToken tokenEntity = userOptional.get()
            .getTokens()
            .stream()
            .filter(identityValidationToken -> identityValidationToken.getId().equals(token))
            .findFirst().get();

        if (tokenEntity.isVerified() || tokenEntity.getExpiration().isBefore(LocalDateTime.now())) {
            return false;
        }

        tokenEntity.setVerified(true);
        repository.save(userOptional.get());

        return true;
    }

    @Override
    public final Optional<UserDto> getUserWithToken(UUID token) {
        Optional<User> optionalUser = repository.findUserWithToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            return Optional.of(userMapper.mapTo(user));
        }

        return Optional.empty();
    }
}
