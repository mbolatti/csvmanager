package com.csvmanager.auth.config;

import com.csvmanager.auth.service.JWTService;
import com.csvmanager.auth.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private final JWTService jwtService;
  @Autowired
  UserDetailsService userDetailsService;

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // bearer token
    final String jwtToken;
    final String userEmail;
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    jwtToken = authHeader.substring(BEARER_PREFIX.length());
    userEmail = jwtService.extractUserEmail(jwtToken);// extract userEmail from JWT token
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetailsImpl userDetails = (UserDetailsImpl) this.userDetailsService.loadUserByUsername(
          userEmail);
      if (jwtService.isTokenValid(jwtToken, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);

  }
}
