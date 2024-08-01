package com.csvmanager.auth.service;

import com.csvmanager.auth.domain.model.User;
import com.csvmanager.auth.service.model.AuthRequest;
import com.csvmanager.auth.service.model.AuthenticationReponse;
import com.csvmanager.auth.service.model.EmailExistsException;
import com.csvmanager.auth.service.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  @Autowired
  private UserService userService;

  private final JWTService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationReponse register(UserDto request)
      throws EmailExistsException {
    User user = userService.registerNewUserAccount(request);
    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    var jwtToken = jwtService.generateToken(userDetails);
    return AuthenticationReponse.builder().token(jwtToken).build();
  }

  public AuthenticationReponse authenticate(AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    var user = userService.findAUserAccount(request.getEmail())
        .orElseThrow();
    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    var jwtToken = jwtService.generateToken(userDetails);
    return AuthenticationReponse.builder().token(jwtToken).build();
  }
}
