package com.csvmanager.application.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportResultDto {
  private Integer total = 0;
  private Integer imported = 0;
  private List<ValidationLineResultDto> errors = new ArrayList<>();
}