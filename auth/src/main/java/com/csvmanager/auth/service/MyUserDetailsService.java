package com.csvmanager.auth.service;

import com.csvmanager.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public MyUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    return userRepository.findByEmail(email)
        .map(UserDetailsImpl::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
  }
}