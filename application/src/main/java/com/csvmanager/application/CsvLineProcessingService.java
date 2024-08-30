package com.csvmanager.application;

import com.csvmanager.domain.jpa.async.FileProcess;
import com.csvmanager.domain.jpa.async.LineData;
import com.csvmanager.domain.jpa.async.ProcessStatus;
import com.csvmanager.domain.model.PersonalData;
import com.csvmanager.domain.port.out.CsvParser;
import com.csvmanager.domain.port.out.FileProcessPort;
import com.csvmanager.domain.port.out.LineDataPort;
import com.csvmanager.domain.port.out.PersonalDataPort;
import com.csvmanager.application.service.Messaging;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class CsvLineProcessingService {

  public static final String RECORD_ALREADY_EXISTS_IN_THE_DATABASE = "The record already exists in the database";
  public static final String ERROR_DOWNLOADING_CSV_THE_FILE = "Error downloading csv the file";
  public static String TYPE = "text/csv";

  private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

  private final PersonalDataPort personalDataPort;
  private final FileProcessPort fileProcessPort;
  private final LineDataPort lineDataPort;

  public CsvLineProcessingService(PersonalDataPort personalDataPort,
      FileProcessPort fileProcessPort, LineDataPort lineDataPort, CsvParser csvParser,
      Messaging messaging) {
    this.personalDataPort = personalDataPort;
    this.fileProcessPort = fileProcessPort;
    this.lineDataPort = lineDataPort;
  }

  @RabbitListener(queues = "#{config.queue1}")
  public void extractAndSaveLine(String fileProcessId) {
    System.out.println(
        "Received message to start processing the saved lines for file <" + fileProcessId + ">");
    FileProcess fileProcess = fileProcessPort.findById(fileProcessId);
    List<LineData> lineDataList = lineDataPort.findByFileProcess(fileProcess.getId());
    List<Boolean> processedLines = lineDataList.stream()
        .map(lineData -> validateLine(lineData, fileProcess)).toList();
    long wrongLines = processedLines.stream().filter(val -> val.equals(false)).count();
    if (wrongLines == 0) {
      fileProcess.setStatus(ProcessStatus.PROCESSING_OK);
    } else {
      fileProcess.setStatus(ProcessStatus.PROCESSING_WITH_ERROR);
    }
    fileProcessPort.merge(fileProcess);
  }

  private boolean validateLine(LineData lineData, FileProcess fileProcess) {
    boolean processedOk = true;
    PersonalData personalData = new PersonalData(lineData.getLineNumber(),
        lineData.getFileProcess().getId().toString(), lineData.getName(),
        lineData.getLastName(),
        lineData.getBirthDate(), lineData.getCity(), lineData.getFiscalCode());
    Set<ConstraintViolation<PersonalData>> violations = validatorFactory.getValidator()
        .validate(personalData);
    if (!violations.isEmpty()) {
      lineData.setErrors(violations.stream().map(ConstraintViolation::getMessage).toList());
      lineData.setStatus(ProcessStatus.PROCESSING_ERROR);
      processedOk = false;
      lineDataPort.save(lineData);
      System.out.println("Errors: " + lineData.getErrors());
    } else {
      Optional<com.csvmanager.domain.jpa.PersonalData> optionalPersonalData = personalDataPort.findByImportDateCityFC(
          fileProcess.getImportDate(), personalData.getCity(), personalData.getFiscalCode());
      if (optionalPersonalData.isPresent()) {
        lineData.setErrors(List.of(RECORD_ALREADY_EXISTS_IN_THE_DATABASE));
        lineData.setStatus(ProcessStatus.PROCESSING_ERROR);
        processedOk = false;
        lineDataPort.save(lineData);
      } else {
        fileProcess.setStatus(ProcessStatus.PROCESSING_OK);
        personalDataPort.save(map(personalData, fileProcess.getImportDate()));
      }
    }
    return processedOk;
  }

  com.csvmanager.domain.jpa.PersonalData map(PersonalData personalData, LocalDate localDate) {
    return com.csvmanager.domain.jpa.PersonalData.builder()
        .importId(personalData.getImportId())
        .importDate(localDate)
        .name(personalData.getName())
        .lastName(personalData.getLastName())
        .birthDate(personalData.getBirthDate())
        .city(personalData.getCity())
        .fiscalCode(personalData.getFiscalCode(
        ))
        .build();
  }
}