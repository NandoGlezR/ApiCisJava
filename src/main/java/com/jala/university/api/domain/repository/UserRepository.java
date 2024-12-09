package com.jala.university.api.domain.repository;

import com.jala.university.api.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {
  User findByLogin(String login);

  @Query("{ 'tokens.id': ?0 }")
  Optional<User> findUserWithToken(UUID token);
}
