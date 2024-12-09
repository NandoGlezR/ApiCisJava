package com.jala.university.api.application.service.impl;

import com.jala.university.api.application.dto.IdentityValidationTokenDto;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.factories.IdentityTokenFactory;
import com.jala.university.api.application.mapper.impl.IdentityValidationTokenMapper;
import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.TokenService;
import com.jala.university.api.domain.entity.IdentityValidationToken;
import com.jala.university.api.domain.repository.IdentityValidationTokenRepository;
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
    private IdentityValidationTokenRepository repository;
    private IdentityTokenFactory tokenFactory;

    @Autowired
    public TokenServiceImpl(IdentityValidationTokenMapper mapper,
                            UserMapper userMapper,
                            IdentityValidationTokenRepository repository) {
        this.mapper = mapper;
        this.userMapper = userMapper;
        this.repository = repository;
        this.tokenFactory = new IdentityTokenFactory();
    }

    @Override
    public final IdentityValidationTokenDto createToken(LocalDateTime expiration, UserDto user)
            throws InvalidParameterException {
        if (expiration.isBefore(LocalDateTime.now()) || expiration.isEqual(LocalDateTime.now())) {
            throw new InvalidParameterException("Expiration time must be after now");
        }

        IdentityValidationToken token = tokenFactory.create(expiration, userMapper.mapFrom(user));

        return mapper.mapTo(repository.save(token));
    }

    @Override
    public final boolean verifyToken(UUID token) {
        Optional<IdentityValidationToken> tokenOptional = repository.findById(token);

        if (tokenOptional.isEmpty()) {
            return false;
        }

        IdentityValidationToken tokenEntity = tokenOptional.get();

        if (tokenEntity.isVerified() || tokenEntity.getExpiration().isBefore(LocalDateTime.now())) {
            return false;
        }

        tokenEntity.setVerified(true);
        repository.save(tokenEntity);

        return true;
    }

    @Override
    public final Optional<IdentityValidationTokenDto> getToken(UUID token) {
        Optional<IdentityValidationToken> optionalToken = repository.findById(token);

        if (optionalToken.isPresent()) {
            IdentityValidationToken tokenEntity = optionalToken.get();

            return Optional.of(mapper.mapTo(tokenEntity));
        }

        return Optional.empty();
    }
}
