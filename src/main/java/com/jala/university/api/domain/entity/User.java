package com.jala.university.api.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {
  @Id
  private String id;

  private String name;

  private String login;

  private String password;

  private boolean validated;

  private List<IdentityValidationToken> tokens = new ArrayList<>();
}
