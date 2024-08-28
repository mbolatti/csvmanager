package com.csvmanager.auth.repository;

import com.csvmanager.auth.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
  Optional<User> findByEmail(@Param("email") String email);

  User findByFirstName(String name);

}
