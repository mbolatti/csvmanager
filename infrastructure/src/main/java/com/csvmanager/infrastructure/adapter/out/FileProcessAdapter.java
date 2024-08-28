package com.csvmanager.infrastructure.adapter.out;

import com.csvmanager.domain.jpa.async.FileProcess;
import com.csvmanager.domain.port.out.FileProcessPort;
import com.csvmanager.infrastructure.repository.FileProcessRepository;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileProcessAdapter implements FileProcessPort {

  private final FileProcessRepository fileProcessRepository;

  public FileProcessAdapter(FileProcessRepository fileProcessRepository) {
    this.fileProcessRepository = fileProcessRepository;
  }

  @Override
  public FileProcess save(FileProcess fileProcess) {
    log.info("Saving file process: {}", fileProcess);

    FileProcess file = FileProcess.createNew();
    file.setFileName("someFileName.csv");
    file.setImportDate(LocalDate.now());
    return fileProcessRepository.save(file);
  }

  @Override
  public FileProcess merge(FileProcess fileProcess) {
    log.info("Updating file process: {}", fileProcess);
    return fileProcessRepository.save(fileProcess);
  }

  @Override
  public FileProcess findById(String id) {
    return fileProcessRepository.findById(Long.valueOf(id))
        .orElseThrow(() -> new RuntimeException("File not found"));
  }
}
