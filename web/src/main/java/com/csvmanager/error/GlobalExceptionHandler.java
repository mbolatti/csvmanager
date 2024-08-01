package com.csvmanager.error;

import com.csvmanager.service.exception.CsvFileDownloadException;
import com.csvmanager.service.exception.CsvFileFormatException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(CsvFileFormatException.class)
  public ResponseEntity<ErrorResponse> handleCsvFileFormatException(CsvFileFormatException ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(CsvFileDownloadException.class)
  public ResponseEntity<ErrorResponse> handleCsvFileDownloadException(CsvFileDownloadException ex) {
    log.info("Error downloading the csv file", ex);
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }


  /*@ExceptionHandler(CompletionException.class)
  public ResponseEntity<ErrorResponse> handleCompletionException(CompletionException ex) {
    Throwable rootCause = ex.getCause();

    if (rootCause instanceof CsvFileFormatException) {
      ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } else if (rootCause instanceof CsvFileDownloadException) {
      ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } else {
      ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
      return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }*/


  @Data
  @AllArgsConstructor
  public class ErrorResponse {
    private String message;
  }
}
