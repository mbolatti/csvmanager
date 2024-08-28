package com.csvmanager.infrastructure.repository;

import com.csvmanager.domain.jpa.async.FileProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileProcessRepository extends JpaRepository<FileProcess, Long> {
}
