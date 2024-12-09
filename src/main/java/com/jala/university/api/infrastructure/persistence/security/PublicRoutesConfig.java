package com.jala.university.api.infrastructure.persistence.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public final class PublicRoutesConfig {
  @Value("#{'${public.routes}'.split(',')}")
  private List<String> publicRoutesStrings;
  @Getter
  private List<AntPathRequestMatcher> publicRoutesMatchers;

  public boolean isRequestOnPublicRoute(HttpServletRequest request) {
    return publicRoutesMatchers.stream().anyMatch(requestMatcher -> requestMatcher.matches(request));
  }

  @PostConstruct
  public void init() {
    publicRoutesMatchers = publicRoutesStrings.stream()
        .map(AntPathRequestMatcher::new)
        .toList();
  }
}
