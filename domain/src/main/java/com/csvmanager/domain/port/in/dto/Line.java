package com.csvmanager.domain.port.in.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Line {

  Long lineNumber;
  Map<Integer, String> row;
}
