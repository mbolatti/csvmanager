package com.csvmanager.application.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessResultDto {
  private OKDto ok = new OKDto();
  private List<ValidationLineResultDto> wrong = new ArrayList<>();
  public void addWrong(ValidationLineResultDto wrong) {
    this.wrong.add(wrong);
  }
}