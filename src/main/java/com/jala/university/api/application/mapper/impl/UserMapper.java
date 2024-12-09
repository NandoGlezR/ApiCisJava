package com.jala.university.api.application.mapper.impl;

import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.mapper.Mapper;
import com.jala.university.api.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDto> {

  /**
   * Maps a UserDto object to a User entity.
   *
   * @param userDto the UserDto object
   * @return the mapped User entity
   */
  @Override
  public User mapFrom(UserDto userDto) {
    return User.builder()
    .id(userDto.getId())
    .name(userDto.getName())
    .login(userDto.getEmail())
    .password(userDto.getPassword())
    .build();
  }

  /**
   * Maps a User entity to a UserDto object.
   *
   * @param entity the User entity
   * @return the mapped UserDto object
   */
  @Override
  public UserDto mapTo(User entity) {
    return UserDto.builder()
    .id(entity.getId())
    .name(entity.getName())
    .email(entity.getLogin())
    .password(entity.getPassword())
    .build();
  }
}
