package com.csvmanager.domain.port.in;

import java.io.ByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

public interface ProcessCsvUseCase {

  Object importCsvFile(MultipartFile file);

  Object returnAll();

  ByteArrayOutputStream writePersonalDataToCsv(ByteArrayOutputStream outputStream);

  void sendMessage1(String message);

  Object returnResults();

  Object findById(String id);
}
