package com.jala.university.api.application.mapper.impl;

import com.jala.university.api.application.dto.IdentityValidationTokenDto;
import com.jala.university.api.application.mapper.Mapper;
import com.jala.university.api.domain.entity.IdentityValidationToken;
import org.springframework.stereotype.Component;

@Component
public class IdentityValidationTokenMapper implements
        Mapper<IdentityValidationToken, IdentityValidationTokenDto> {

    private final UserMapper userMapper = new UserMapper();

    /**
     * Maps an IdentityValidationTokenDto into a IdentityValidationToken.
     *
     * @param dto DTO to be mapped.
     * @return an IdentityValidationToken.
     */
    @Override
    public IdentityValidationToken mapFrom(IdentityValidationTokenDto dto) {
        return IdentityValidationToken.builder()
                .id(dto.getToken())
                .user(userMapper.mapFrom(dto.getUser()))
                .expiration(dto.getExpiration())
                .verified(dto.isVerified())
                .build();
    }

    /**
     * Maps an IdentityValidationToken into IdentityValidationTokenDto.
     *
     * @param entity Entity to be mapped.
     * @return an IdentityValidationTokenDto.
     */
    @Override
    public IdentityValidationTokenDto mapTo(IdentityValidationToken entity) {
        return IdentityValidationTokenDto.builder()
                .token(entity.getId())
                .user(userMapper.mapTo(entity.getUser()))
                .expiration(entity.getExpiration())
                .verified(entity.isVerified())
                .build();
    }
}
