package com.csvmanager.domain.port.out;

import com.csvmanager.domain.jpa.async.FileProcess;

public interface FileProcessPort {
  FileProcess save(FileProcess fileProcess);

  FileProcess merge(FileProcess fileProcess);

  FileProcess findById(String id);
}
