package com.csvmanager.auth.service;

import com.csvmanager.auth.domain.model.User;
import com.csvmanager.auth.repository.RoleRepository;
import com.csvmanager.auth.repository.UserRepository;
import com.csvmanager.auth.service.model.EmailExistsException;
import com.csvmanager.auth.service.model.UserDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  PasswordEncoder passwordEncoder;


  @Transactional
  public User registerNewUserAccount(UserDto accountDto) throws EmailExistsException {

    if (emailExist(accountDto.getEmail())) {
      throw new EmailExistsException
          ("There is an account with that email address: " + accountDto.getEmail());
    }
    User user = new User();

    user.setFirstName(accountDto.getFirstName());
    user.setLastName(accountDto.getLastName());
    user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
    user.setEmail(accountDto.getEmail());
    user.setRoles(accountDto.getRolesIds().stream().map(Long::valueOf).map(roleRepository::findById)
        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    return userRepository.save(user);
  }

  public List<User> getAllUserAccounts() {

    return userRepository.findAll();
  }

  public Optional<User> findAUserAccount(String name) {
    return userRepository.findByEmail(name);
  }

  private boolean emailExist(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  public <T> List<User> findAllUserAccounts() {
    return userRepository.findAll();
  }
}
