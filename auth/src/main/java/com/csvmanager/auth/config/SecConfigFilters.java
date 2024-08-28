package com.csvmanager.auth.config;

import com.csvmanager.auth.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecConfigFilters {
  @Autowired
  private JWTAuthFilter jwtAuthFilter;
  @Autowired
  private MyUserDetailsService myUserDetailsService;

  private static final String[] AUTH_WHITE_LIST = {
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/v2/api-docs/**",
      "/swagger-resources/**",
      "/api/v1/public/auth/**",
      "/api/v1/public/messaging/**",
      "/**"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(AUTH_WHITE_LIST).permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS
        ))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(myUserDetailsService);
    authProvider.setPasswordEncoder(encoder());
    return authProvider;
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
