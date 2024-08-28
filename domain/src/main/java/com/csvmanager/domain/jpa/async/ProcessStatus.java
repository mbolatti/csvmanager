package com.csvmanager.domain.jpa.async;

public enum ProcessStatus {
  CREATED,
  PROCESSING,
  PROCESSING_OK,
  PROCESSING_ERROR,
  PROCESSING_WITH_ERROR;
}