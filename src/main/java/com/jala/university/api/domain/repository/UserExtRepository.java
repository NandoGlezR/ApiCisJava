package com.jala.university.api.domain.repository;

import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.entity.UserExt;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExtRepository extends JpaRepository<UserExt, Integer> {
  Optional<UserExt> findByUser(User user);
  void deleteByUser(User user);
}
