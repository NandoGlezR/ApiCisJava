package com.jala.university.api.infrastructure.persistence.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public final class NotRegisteredRoutesFilter extends OncePerRequestFilter {
  private final RequestMappingHandlerMapping requestMappingHandlerMapping;

  @Autowired
  public NotRegisteredRoutesFilter(RequestMappingHandlerMapping requestMappingHandlerMapping) {
    this.requestMappingHandlerMapping = requestMappingHandlerMapping;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (request.getRequestURI().startsWith("/swagger")) {
      filterChain.doFilter(request, response);
    }

    try {
      HandlerExecutionChain handler = requestMappingHandlerMapping.getHandler(request);

      if (handler == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("Route not found");

        return;
      }
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("Route not found");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
