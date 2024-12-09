package com.jala.university.api.domain.exceptions.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthEntryPointException implements AuthenticationEntryPoint {

  /**
   * Handles authentication failures and sends a JSON response indicating the unauthorized access.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param authException the authentication exception
   * @throws  
  ServletException if there is an error writing the JSON response
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws ServletException {
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    body.put("error", "Unauthorized");
    body.put("message", "Access denied: Invalid or missing token");
    body.put("path", request.getServletPath());

    try {
      response.getOutputStream().println(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body));
    } catch (IOException e) {
      throw new ServletException("Error writing JSON response", e);
    }
  }
}
