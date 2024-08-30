package com.csvmanager.domain.port.out;

import com.csvmanager.domain.jpa.async.FileProcess;
import java.util.List;

public interface FileProcessPort {
  FileProcess save(FileProcess fileProcess);

  FileProcess merge(FileProcess fileProcess);

  FileProcess findById(String id);

  List<FileProcess> findAll();
}
