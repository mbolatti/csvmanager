package com.csvmanager.domain.port.in;

import java.io.ByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

public interface ProcessCsvUseCase {

  Object processCsv(MultipartFile file);

  Object returnAll();

  ByteArrayOutputStream writePersonalDataToCsv(ByteArrayOutputStream outputStream);
}
