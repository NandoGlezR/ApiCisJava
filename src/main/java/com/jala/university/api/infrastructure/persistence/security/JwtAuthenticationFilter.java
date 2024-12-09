package com.jala.university.api.infrastructure.persistence.security;

import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.service.UserService;
import com.jala.university.api.domain.exceptions.user.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserService userService;
  private final PublicRoutesConfig publicRoutesConfig;

  @Autowired
  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, JwtTokenUtil jwtTokenUtil,
      @Lazy UserService userService, PublicRoutesConfig publicRoutesConfig) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.jwtTokenUtil = jwtTokenUtil;
    this.userService = userService;
    this.publicRoutesConfig = publicRoutesConfig;
  }

  /**
   * Validates the JWT token in the request header and sets the authentication context if the token
   * is valid.
   *
   * @param request     the HTTP request
   * @param response    the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if there is an error filtering the request
   * @throws IOException      if there is an error filtering the request
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String jwtToken = getTokenFromRequest(request);
    String userId;

    try {
      userId = jwtTokenUtil.getUserIdFromToken(jwtToken);
    } catch (JwtException | IllegalArgumentException ex) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Unauthorized: invalid or missing token");

      return;
    }

    var securityContext = SecurityContextHolder.getContext().getAuthentication();

    if (userId != null && securityContext == null) {
      try {
        UserDto user = userService.getUserById(userId);

        if (jwtTokenProvider.validateToken(jwtToken, userId, jwtTokenUtil)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(user, null, null);

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);

          filterChain.doFilter(request, response);
          return;
        }
      } catch (UserNotFoundException ignored) {
      }
    }

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write("Unauthorized: invalid or missing token");
  }

  @Override
  protected final boolean shouldNotFilter(HttpServletRequest request) {
    return publicRoutesConfig.isRequestOnPublicRoute(request);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(JwtKeyUtil.SPLIT_INDEX);
    }

    return "";
  }
}
