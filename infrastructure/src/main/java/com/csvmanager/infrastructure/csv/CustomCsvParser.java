package com.csvmanager.infrastructure.csv;

import com.csvmanager.domain.port.in.dto.Line;
import com.csvmanager.domain.port.out.CsvParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CustomCsvParser implements CsvParser {

  @Override
  public List<Line> parse(InputStream csvInputStream, Boolean skipHeaders) throws IOException {
    throw new RuntimeException("not implemented");
  }

}
