package com.csvmanager.web.error;

import com.csvmanager.application.service.exception.CsvFileDownloadException;
import com.csvmanager.application.service.exception.CsvFileFormatException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.core.annotation.Order;
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

  @ExceptionHandler({AmqpRejectAndDontRequeueException.class, AmqpException.class})
  @Order(1)
  public ResponseEntity<ErrorResponse> handleMessagingException(Exception ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Data
  @AllArgsConstructor
  public class ErrorResponse {
    private String message;
  }
}
