package com.csvmanager.auth.service.model;

public class EmailExistsException extends Exception {

  public EmailExistsException() {
    super();
  }

  public EmailExistsException(String message) {
    super(message);
  }

  public EmailExistsException(String message, Throwable cause) {
    super(message, cause);
  }

  public EmailExistsException(Throwable cause) {
    super(cause);
  }
}