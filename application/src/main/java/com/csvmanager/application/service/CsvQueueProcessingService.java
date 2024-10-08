package com.csvmanager.application.service;

import com.csvmanager.application.service.dto.PersonalDataDto;
import com.csvmanager.application.service.dto.async.FileProcessDto;
import com.csvmanager.application.service.dto.async.LineDataDto;
import com.csvmanager.application.service.exception.CsvFileDownloadException;
import com.csvmanager.application.service.exception.CsvFileFormatException;
import com.csvmanager.domain.jpa.async.FileProcess;
import com.csvmanager.domain.jpa.async.LineData;
import com.csvmanager.domain.jpa.async.ProcessStatus;
import com.csvmanager.domain.port.in.ProcessCsvUseCase;
import com.csvmanager.domain.port.in.dto.Line;
import com.csvmanager.domain.port.out.CsvParser;
import com.csvmanager.domain.port.out.FileProcessPort;
import com.csvmanager.domain.port.out.LineDataPort;
import com.csvmanager.domain.port.out.PersonalDataPort;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class CsvQueueProcessingService implements ProcessCsvUseCase {

  public static final String RECORD_ALREADY_EXISTS_IN_THE_DATABASE = "The record already exists in the database";
  public static final String ERROR_DOWNLOADING_CSV_THE_FILE = "Error downloading csv the file";
  public static final int NAME_POSITION = 0;
  public static final int LAST_NAME_POSITION = 1;
  public static final int BIRTHDAY_POSITION = 2;
  public static final int CITY_POSITION = 3;
  public static final int FISCAL_CODE_POSITION = 4;
  public static String TYPE = "text/csv";

  private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

  private final PersonalDataPort personalDataPort;
  private final FileProcessPort fileProcessPort;
  private final LineDataPort lineDataPort;
  private final CsvParser csvParser;
  private final Messaging messaging;

  public CsvQueueProcessingService(PersonalDataPort personalDataPort,
      FileProcessPort fileProcessPort, LineDataPort lineDataPort, CsvParser csvParser,
      Messaging messaging) {
    this.personalDataPort = personalDataPort;
    this.fileProcessPort = fileProcessPort;
    this.lineDataPort = lineDataPort;
    this.csvParser = csvParser;
    this.messaging = messaging;
  }

  @Override
  @Transactional
  public Object importCsvFile(MultipartFile file) {
    log.info("processing CSV file: {} with class {}", file.getOriginalFilename(),
        this.getClass().getName());
    boolean skipHeaders = true;
    try {
      if (hasCSVFormat(file)) {
        LocalDate importDate = LocalDate.now();
        FileProcessDto fileProcessDto = FileProcessDto.createNew();
        fileProcessDto.setImportDate(importDate);
        fileProcessDto.setFileName(file.getOriginalFilename());

        List<LineDataDto> lineDataDtoList = csvParser.parse(
                file.getInputStream(),
                skipHeaders).stream()
            .map(this::lineToLineDataDto).toList();

        FileProcess fileProcess = fileProcessPort.save(fileProcessDtoToFileProcess(fileProcessDto));

        lineDataPort.saveAll(
            lineDataDtoList.stream().map(lineDto -> lineDataDtoToLineData(lineDto, fileProcess))
                .toList());

        // send message to let know the loading process is over and should start the next one
        sendMessage1(fileProcess.getId().toString());
        return Map.of("fileProcessId", fileProcess.getId());
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
    return getPersonalData();
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

  @Override
  public void sendMessage1(String message) {
    messaging.sendMessage1(message);
  }

  @Override
  public Object returnResults() {
    return fileProcessPort.findAll().stream()
        .map(fileResult -> this.fileProcessJpaToFileProcessDto(fileResult, false))
        .collect(Collectors.toList());
  }

  @Override
  public Object findById(String id) {
    return fileProcessJpaToFileProcessDto(fileProcessPort.findById(id), true);
  }

  private FileProcessDto fileProcessJpaToFileProcessDto(FileProcess fileProcess,
      boolean addLineData) {
    FileProcessDto fileProcessDto = new FileProcessDto();
    fileProcessDto.setId(fileProcess.getId());
    fileProcessDto.setFileName(fileProcess.getFileName());
    fileProcessDto.setImportDate(fileProcess.getImportDate());
    fileProcessDto.setStatus(fileProcess.getStatus());
    if (addLineData) {
      List<LineDataDto> lineDataDtos = lineDataPort.findByFileProcess(fileProcess.getId())
          .stream()
          .map(this::lineDataToLineDataDto)
          .collect(Collectors.toList());
      fileProcessDto.setLineDataList(lineDataDtos);
    } else {
      fileProcessDto.setLineDataList(List.of());
    }
    return fileProcessDto;
  }

  private LineDataDto lineDataToLineDataDto(LineData lineData) {
    LineDataDto lineDataDto = new LineDataDto();
    lineDataDto.setId(lineData.getId());
    lineDataDto.setLineNumber(lineData.getLineNumber());
    lineDataDto.setName(lineData.getName());
    lineDataDto.setLastName(lineData.getLastName());
    lineDataDto.setBirthDate(lineData.getBirthDate());
    lineDataDto.setCity(lineData.getCity());
    lineDataDto.setFiscalCode(lineData.getFiscalCode());
    lineDataDto.setStatus(lineData.getStatus());
    lineDataDto.setErrors(lineData.getErrors());
    return lineDataDto;
  }

  private List<PersonalDataDto> getPersonalData() {
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

  private LineData lineDataDtoToLineData(LineDataDto lineDto, FileProcess fileProcess) {
    return LineData
        .builder()
        .lineNumber(lineDto.getLineNumber())
        .fileProcess(fileProcess)
        .name(lineDto.getName())
        .lastName(lineDto.getLastName())
        .birthDate(lineDto.getBirthDate())
        .city(lineDto.getCity())
        .fiscalCode(lineDto.getFiscalCode())
        .status(lineDto.getStatus())
        .build();
  }

  private LineDataDto lineToLineDataDto(Line line) {
    return LineDataDto
        .builder()
        .lineNumber(line.getLineNumber())
        .name(line.getRow().get(NAME_POSITION))
        .lastName(line.getRow().get(LAST_NAME_POSITION))
        .birthDate(line.getRow().get(BIRTHDAY_POSITION))
        .city(line.getRow().get(CITY_POSITION))
        .fiscalCode(line.getRow().get(FISCAL_CODE_POSITION))
        .status(ProcessStatus.CREATED)
        .build();
  }

  private FileProcess fileProcessDtoToFileProcess(FileProcessDto fileProcessDto) {
    FileProcess fileProcess = new FileProcess();
    fileProcess.setFileName(fileProcessDto.getFileName());
    fileProcess.setImportDate(fileProcessDto.getImportDate());
    fileProcess.setStatus(fileProcessDto.getStatus());
    return fileProcess;
  }
}