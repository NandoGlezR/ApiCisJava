package com.jala.university.api.application.service.impl;

import com.jala.university.api.domain.repository.IdentityValidationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Perform cleanup of unused/expired data.
 */
@Service
public class CleanupService {
  private IdentityValidationTokenRepository tokenRepository;

  @Autowired
  public CleanupService(IdentityValidationTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  /**
   * Deletes all expired tokens every first day of the mont.
   */
  @Transactional
  @Scheduled(cron = "0 0 0 1 * ?")
  public void deleteExpiredTokens() {
    tokenRepository.deleteExpired();
  }
}
