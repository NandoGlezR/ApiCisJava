package com.jala.university.api.infrastructure.persistence.utils;

import com.jala.university.api.infrastructure.persistence.security.JwtAuthenticationFilter;
import com.jala.university.api.infrastructure.persistence.security.NotRegisteredRoutesFilter;
import com.jala.university.api.infrastructure.persistence.security.PublicRoutesConfig;
import com.jala.university.api.infrastructure.persistence.security.UserAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig is a configuration class for Spring Security. It sets up the security filter
 * chain, password encoder, and authentication manager.
 * <p>
 * This class can be extended if additional security configurations are needed.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserAuthorizationFilter userAuthorizationFilter;
  private final NotRegisteredRoutesFilter notRegisteredRoutesFilter;
  private final PublicRoutesConfig publicRoutesConfig;

  /**
   * Constructs a SecurityConfig instance with the specified JWT authentication filter and JWT
   * authentication entry point.
   *
   * @param jwtAuthenticationFilter JWT authentication filter
   * @param userAuthorizationFilter user authorization filter
   * @param publicRoutesConfig      contains public routes URIs
   * @param notRegisteredRoutesFilter not registered routes filter
   */
  @Autowired
  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
      UserAuthorizationFilter userAuthorizationFilter,
      PublicRoutesConfig publicRoutesConfig,
      NotRegisteredRoutesFilter notRegisteredRoutesFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.userAuthorizationFilter = userAuthorizationFilter;
    this.publicRoutesConfig = publicRoutesConfig;
    this.notRegisteredRoutesFilter = notRegisteredRoutesFilter;
  }

  /**
   * Configures the security filter chain for HTTP requests. This includes disabling CSRF, setting
   * up exception handling, configuring session management, and defining access rules.
   *
   * @param http the HttpSecurity object for configuring HTTP security
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> {
          publicRoutesConfig.getPublicRoutesMatchers()
              .forEach(route -> authorize.requestMatchers(route).permitAll());

          authorize.anyRequest().authenticated();
        })
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(notRegisteredRoutesFilter, JwtAuthenticationFilter.class)
        .addFilterAfter(userAuthorizationFilter, JwtAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Provides a PasswordEncoder bean for encoding passwords.
   *
   * @return the PasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Provides an AuthenticationManager bean for managing authentication.
   *
   * @param authenticationConfiguration the AuthenticationConfiguration object
   * @return the AuthenticationManager instance
   * @throws Exception if an error occurs while getting the AuthenticationManager
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}


