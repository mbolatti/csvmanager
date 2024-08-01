package com.csvmanager.auth.repository;

import com.csvmanager.auth.domain.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

  Privilege findByName(String name);

}
