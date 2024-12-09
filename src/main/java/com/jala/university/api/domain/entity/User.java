package com.jala.university.api.domain.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User {
  @Id
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String login;

  @Column(nullable = false)
  private String password;

  @OneToMany(mappedBy = "user", targetEntity = IdentityValidationToken.class, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  private List<IdentityValidationToken> tokens;

  @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, targetEntity = UserExt.class, cascade = CascadeType.REMOVE)
  private UserExt userExt;
}
