package com.jala.university.api.infrastructure.persistence.security;

import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public final class UserAuthorizationFilter extends OncePerRequestFilter {

  private final PublicRoutesConfig publicRoutesConfig;
  private final UserRepository userRepository;

  @Autowired
  public UserAuthorizationFilter(PublicRoutesConfig publicRoutesConfig, UserRepository userRepository) {
    this.publicRoutesConfig = publicRoutesConfig;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String userIdOnJwt = getIdFromJwtOnSecurityContext();
    String userIdOnUri = getUserIdFromUri(request.getRequestURI());

    if (userIdOnJwt == null || !userRepository.existsById(userIdOnUri)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("User not found");
      return;
    }

    if (!userIdOnJwt.equals(userIdOnUri)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write("Forbidden: You are not allowed to access this resource");
      return;
    }


    filterChain.doFilter(request, response);
  }

  private String getIdFromJwtOnSecurityContext() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.getPrincipal() instanceof UserDto dto) {
      return dto.getId();
    }

    return null;
  }

  private String getUserIdFromUri(String uri) {
    return uri.split("/")[2];
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return publicRoutesConfig.isRequestOnPublicRoute(request);
  }
}
