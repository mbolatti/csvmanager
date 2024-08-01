package com.csvmanager.infrastructure.csv;

import com.csvmanager.domain.port.in.dto.Line;
import com.csvmanager.domain.port.out.CsvParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class ApacheCommonsCsvParser implements CsvParser {

  @Override
  public List<Line> parse(InputStream csvInputStream, Boolean skipHeaders) throws IOException {
    Reader reader = new InputStreamReader(csvInputStream);
    List<Line> result = new ArrayList<>();
    CSVParser lines = new CSVParser(reader,
        CSVFormat.RFC4180.builder().setSkipHeaderRecord(skipHeaders).setTrim(true)
            .setIgnoreSurroundingSpaces(true)
            .setIgnoreEmptyLines(true).build());
    for (CSVRecord line : lines) {
      Map<Integer, String> row = new HashMap<>();
      for (int i = 0; i < line.size(); i++) {
        row.put(i, line.get(i));
      }
      result.add(new Line(line.getRecordNumber(), row));
    }
    return result;
  }

}
