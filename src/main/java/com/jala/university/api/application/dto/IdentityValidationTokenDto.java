package com.jala.university.api.application.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class IdentityValidationTokenDto {
    private String token;
    private LocalDateTime expiration;
    private boolean verified;
}
