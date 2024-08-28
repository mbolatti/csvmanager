package com.csvmanager.infrastructure.adapter.out;

import com.csvmanager.domain.jpa.async.LineData;
import com.csvmanager.domain.port.out.LineDataPort;
import com.csvmanager.infrastructure.repository.LineDataRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LineDataAdapter implements LineDataPort {

  private final LineDataRepository lineDataRepository;

  public LineDataAdapter(LineDataRepository lineDataRepository) {
    this.lineDataRepository = lineDataRepository;
  }

  @Override
  public LineData save(LineData lineData) {
    log.info("Saving lineData: {}", lineData);
    return lineDataRepository.save(lineData);
  }

  @Override
  public List<LineData> saveAll(List<LineData> lineDatas) {
    log.info("Saving lineData list: {}", lineDatas);
    return lineDataRepository.saveAll(lineDatas);
  }

  @Override
  public List<LineData> findByFileProcess(Long processId) {
    return lineDataRepository.findByFileProcess(processId);
  }
}
