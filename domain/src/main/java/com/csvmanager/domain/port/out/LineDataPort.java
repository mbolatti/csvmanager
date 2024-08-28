package com.csvmanager.domain.port.out;

import com.csvmanager.domain.jpa.async.LineData;
import java.util.List;

public interface LineDataPort {
  LineData save(LineData lineData);

  List<LineData> saveAll(List<LineData> lineDatas);

  List<LineData> findByFileProcess(Long processId);
}
