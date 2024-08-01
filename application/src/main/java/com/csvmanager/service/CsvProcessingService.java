package com.csvmanager.service;

import com.csvmanager.domain.model.PersonalData;
import com.csvmanager.domain.port.in.ProcessCsvUseCase;
import com.csvmanager.domain.port.in.dto.Line;
import com.csvmanager.domain.port.out.CsvParser;
import com.csvmanager.domain.port.out.PersonalDataPort;
import com.csvmanager.service.dto.PersonalDataDto;
import com.csvmanager.service.dto.ProcessResultDto;
import com.csvmanager.service.dto.ValidationLineResultDto;
import com.csvmanager.service.exception.CsvFileDownloadException;
import com.csvmanager.service.exception.CsvFileFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvProcessingService implements ProcessCsvUseCase {

  public static final String RECORD_ALREADY_EXISTS_IN_THE_DATABASE = "The record already exists in the database";
  public static final String ERROR_DOWNLOADING_CSV_THE_FILE = "Error downloading csv the file";
  public static String TYPE = "text/csv";

  private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

  private final PersonalDataPort personalDataPort;
  private final CsvParser csvParser;

  public CsvProcessingService(PersonalDataPort personalDataPort, CsvParser csvParser) {
    this.personalDataPort = personalDataPort;
    this.csvParser = csvParser;
  }

  @Override
  @Transactional
  public Object processCsv(MultipartFile file) {
    boolean skipHeaders = true;
    try {
      if (hasCSVFormat(file)) {
        ProcessResultDto result = new ProcessResultDto();
        UUID uuid = UUID.randomUUID();
        LocalDate importDate = LocalDate.now();
        List<CompletableFuture<ProcessResultDto>> futureList = csvParser.parse(
                file.getInputStream(),
                skipHeaders).stream()
            .map(line -> CompletableFuture.supplyAsync(() -> {
              return extractAndSaveLine(line, uuid, importDate, result);
            }))
            .toList();
        CompletableFuture<ProcessResultDto> resultFuture = CompletableFuture.allOf(
                futureList.toArray(new CompletableFuture[0]))
            .thenApply(v -> result);

        return resultFuture.join();
      } else {
        throw new CsvFileFormatException(
            String.format("Error in the csv file format - filename: %s",
                file.getOriginalFilename()));
      }
    } catch (IOException e) {
      throw new CsvFileFormatException(
          String.format("Fail trying to process csv data: %s from file: %s", e.getMessage(),
              file.getOriginalFilename()));
    }
  }

  @Override
  public Object returnAll() {
    return getPersonalDatas();
  }

  @Override
  public ByteArrayOutputStream writePersonalDataToCsv(ByteArrayOutputStream outputStream) {
    try (CSVPrinter printer = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT)) {
      List<PersonalDataDto> personalDataDtos = (List<PersonalDataDto>) returnAll();
      for (PersonalDataDto personalDataDto : personalDataDtos) {
        printer.printRecord(personalDataDto.getName(), personalDataDto.getLastName(),
            personalDataDto.getBirthDate(), personalDataDto.getCity(),
            personalDataDto.getFiscalCode());
      }
    } catch (Exception e) {
      throw new CsvFileDownloadException(ERROR_DOWNLOADING_CSV_THE_FILE);
    }
    return outputStream;
  }

  private List<PersonalDataDto> getPersonalDatas() {
    return personalDataPort.findAllImportedCsv().stream().map(a ->
        PersonalDataDto.builder()
            .id(a.getId())
            .name(a.getName())
            .lastName(a.getLastName())
            .importId(a.getImportId())
            .birthDate(a.getBirthDate())
            .city(a.getCity())
            .fiscalCode(a.getFiscalCode())
            .importDate(a.getImportDate())
            .build()
    ).collect(Collectors.toList());
  }

  private boolean hasCSVFormat(MultipartFile file) {
    return TYPE.equals(file.getContentType());
  }

  private ValidationLineResultDto validateLine(PersonalData personalData, LocalDate importDate) {
    ValidationLineResultDto validationLineResultDto;
    Set<ConstraintViolation<PersonalData>> violations = validatorFactory.getValidator()
        .validate(personalData);
    if (!violations.isEmpty()) {
      validationLineResultDto = generateValidationResultDto(personalData,
          violations.stream().map(ConstraintViolation::getMessage).toList());
    } else {
      Optional<com.csvmanager.domain.jpa.PersonalData> optionalPersonalData = personalDataPort.findByImportDateCityFC(
          importDate, personalData.getCity(), personalData.getFiscalCode());
      if (optionalPersonalData.isPresent()) {
        validationLineResultDto = generateValidationResultDto(personalData,
            List.of(RECORD_ALREADY_EXISTS_IN_THE_DATABASE));
      } else {
        validationLineResultDto = generateValidationResultDto(personalData, List.of());
      }
    }
    return validationLineResultDto;
  }

  private static ValidationLineResultDto generateValidationResultDto(PersonalData personalData,
      List<String> errorsList) {
    return ValidationLineResultDto
        .builder()
        .lineNumber(personalData.getLineId())
        .content(personalData.toString())
        .errors(errorsList)
        .build();
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

  private ProcessResultDto extractAndSaveLine(Line line, UUID uuid,
      LocalDate importDate, ProcessResultDto result) {
    PersonalData personalData = new PersonalData(line.getLineNumber(), uuid.toString(),
        line.getRow().get(0), line.getRow().get(1), line.getRow().get(2), line.getRow().get(3),
        line.getRow().get(4));
    ValidationLineResultDto validationLineResult = validateLine(personalData, importDate);
    if (validationLineResult.getErrors().isEmpty()) {
      try {
        personalDataPort.save(map(personalData, importDate));
        result.getOk().addOK(personalData.toString());
      } catch (Exception e) {
        result.addWrong(ValidationLineResultDto.builder()
            .lineNumber(personalData.getLineId())
            .content(personalData.toString())
            .errors(List.of(RECORD_ALREADY_EXISTS_IN_THE_DATABASE))
            .build());
      }
    } else {
      result.addWrong(validationLineResult);
    }
    return result;
  }
}