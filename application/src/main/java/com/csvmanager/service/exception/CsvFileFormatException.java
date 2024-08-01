package com.csvmanager.service.exception;

public class CsvFileFormatException extends RuntimeException {

  public CsvFileFormatException() {
    super("Error in the csv file format");
  }

  public CsvFileFormatException(String message) {
    super(message);
  }
}


