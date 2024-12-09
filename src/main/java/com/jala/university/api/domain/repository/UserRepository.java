package com.jala.university.api.domain.repository;

import com.jala.university.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  User findByLogin(String login);
}
