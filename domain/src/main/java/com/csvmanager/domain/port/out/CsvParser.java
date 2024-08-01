package com.csvmanager.domain.port.out;

import com.csvmanager.domain.port.in.dto.Line;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface CsvParser {
  List<Line> parse(InputStream csvInputStream, Boolean skipHeaders) throws IOException;
}
