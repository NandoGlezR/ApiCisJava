package com.jala.university.api.application.mapper;

public interface Mapper<E, D> {

  /**
   * Maps a DTO into an Entity.
   *
   * @param dto DTO to be mapped.
   * @return Entity mapped from dto.
   */
  E mapFrom(D dto);

  /**
   * Maps an Entity into a DTO.
   *
   * @param entity Entity to be mapped.
   * @return DTO mapped from the DTO.
   */
  D mapTo(E entity);
}
