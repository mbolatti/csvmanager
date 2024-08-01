package com.csvmanager.controller;

import com.csvmanager.auth.service.AuthService;
import com.csvmanager.auth.service.model.AuthRequest;
import com.csvmanager.auth.service.model.AuthenticationReponse;
import com.csvmanager.auth.service.model.EmailExistsException;
import com.csvmanager.auth.service.model.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/auth/")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@Slf4j
public class AuthrenticationController {
  private final AuthService authService;

  @PostMapping("register")
  public ResponseEntity<AuthenticationReponse> register(@Valid @RequestBody UserDto request) throws EmailExistsException {
    log.info("registering user");
    AuthenticationReponse authenticationReponse = authService.register(request);
    return ResponseEntity.accepted().body(authenticationReponse);
  }

  @PostMapping("login")
  public ResponseEntity<AuthenticationReponse> authenticate(@Valid @RequestBody AuthRequest request) {
    log.info("authenticating user");
    return ResponseEntity.ok(authService.authenticate(request));
  }

}
