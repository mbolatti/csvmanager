package com.csvmanager.application.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationLineResultDto {
  private Long lineNumber = 0L;
  private String content = "";
  private List<String> errors = new ArrayList<>();
}