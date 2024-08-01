
package com.csvmanager.controller;

import com.csvmanager.domain.port.in.ProcessCsvUseCase;
import com.csvmanager.service.dto.PersonalDataDto;
import com.csvmanager.service.dto.ProcessResultDto;
import com.csvmanager.service.dto.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayOutputStream;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/private/data/")
@Tag(name = "CSV data management", description = "Manage CSV data")
@Slf4j
public class CsvImportController {
  private final ProcessCsvUseCase processCsvUseCase;

  @Autowired
  public CsvImportController(ProcessCsvUseCase processCsvUseCase) {
    this.processCsvUseCase = processCsvUseCase;
  }

  @PostMapping(path = "import", consumes = "multipart/form-data")
  @Operation(summary = "Import CSV data", description = "Imports CSV data from a file")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "CSV import successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessResultDto.class))),
      @ApiResponse(responseCode = "400", description = "Invalid CSV file"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<Object> importCsv(@RequestParam("file") MultipartFile file) {
    log.info("uploading CSV file");
    ProcessResultDto result = (ProcessResultDto) processCsvUseCase.processCsv(file);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @GetMapping(path = "import", produces = "application/json")
  @Operation(summary = "Get All CSV imports", description = "Return all the imported CSVs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Get all the imported CSVs", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PersonalDataDto.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @JsonView(Views.Public.class)
  public ResponseEntity<Object> getAllImportedCSV() {
    log.info("requested all the imported CSVs");
    List<PersonalDataDto> result = (List<PersonalDataDto>) processCsvUseCase.returnAll();
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @SneakyThrows
  @GetMapping(path = "export", produces = "text/csv")
  @Operation(summary = "Download a file with All CSV imports", description = "Return a CSV file with all the imported CSVs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Get the file with all the imported CSVs",
          content = @Content(mediaType = "text/csv", array = @ArraySchema(schema = @Schema(implementation = PersonalDataDto.class)))),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @JsonView(Views.Public.class)
  public ResponseEntity<byte[]> exportCsv() {
    log.info("requested csv to export");
    ByteArrayOutputStream outputStream = processCsvUseCase.writePersonalDataToCsv(
        new ByteArrayOutputStream());
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"export.csv\"")
        .body(outputStream.toByteArray());
  }
}
