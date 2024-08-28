package com.csvmanager.application.service.dto.async;

import com.csvmanager.domain.jpa.async.ProcessStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class LineDataDto {
  private Long id;
  private FileProcessDto fileProcess;
  private Long lineNumber;
  private String name;
  private String lastName;
  private String birthDate;
  private String city;
  private String fiscalCode;
  @Enumerated(EnumType.STRING)
  private ProcessStatus status;
  private List<String> errors;
}