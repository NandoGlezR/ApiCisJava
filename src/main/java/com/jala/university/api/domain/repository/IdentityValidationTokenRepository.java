package com.jala.university.api.domain.repository;

import com.jala.university.api.domain.entity.IdentityValidationToken;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IdentityValidationTokenRepository extends
    JpaRepository<IdentityValidationToken, UUID> {
  @Modifying
  @Query("DELETE FROM IdentityValidationToken t WHERE t.expiration < CURRENT_TIMESTAMP")
  void deleteExpired();
}
