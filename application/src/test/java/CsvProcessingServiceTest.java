import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.csvmanager.domain.jpa.PersonalData;
import com.csvmanager.domain.port.in.dto.Line;
import com.csvmanager.domain.port.out.CsvParser;
import com.csvmanager.domain.port.out.PersonalDataPort;
import com.csvmanager.application.service.CsvProcessingService;
import com.csvmanager.application.service.dto.ProcessResultDto;
import com.csvmanager.application.service.exception.CsvFileFormatException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;


public class CsvProcessingServiceTest {
  @Mock
  private PersonalDataPort personalDataPort;
  @Mock
  private CsvParser csvParser;

  @InjectMocks
  private CsvProcessingService csvProcessingService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @Timeout(value = 5)
  @Transactional
  public void shouldProcessValidCsvLine() throws IOException {
    String csvContent = "Name1,Surname19,23/04/2001,Naples,BLAJTN12L23F600Z";
    Map<Integer, String> row = new HashMap<>();
    row.put(0, "Name1");
    row.put(1, "Surname19");
    row.put(2, "23/04/2001");
    row.put(3, "Naples");
    row.put(4, "BLAJTN12L23F600Z");
    Line line = new Line(1L,row);
    MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", "test.csv", "text/csv",
            csvContent.getBytes(StandardCharsets.UTF_8));

    doNothing().when(personalDataPort).save(any(PersonalData.class));

    when(csvParser.parse(any(), anyBoolean())).thenReturn(List.of(line));

    ProcessResultDto result = (ProcessResultDto) csvProcessingService.importCsvFile(mockMultipartFile);

    assertNotNull(result);
    assertEquals(1, result.getOk().getChargedList().size());
    assertEquals(0, result.getWrong().size());
    verify(personalDataPort, times(1)).save(any(PersonalData.class));

    assertEquals("nome='Name1', cognome='Surname19', DataDiNascita='23/04/2001', città='Naples', codiceFiscale='BLAJTN12L23F600Z'", result.getOk().getChargedList().get(0));
  }

  @Test
  public void shouldHandleInvalidCsvContentFormat() throws IOException {
    String csvContent = "invalid format";
    MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", "test.csv", "text/csv",
            csvContent.getBytes(StandardCharsets.UTF_8));

    when(csvParser.parse(any(), anyBoolean())).thenThrow(new IOException("Invalid CSV format"));

    CsvProcessingService service = new CsvProcessingService(personalDataPort, csvParser, null);
    assertThrows(CsvFileFormatException.class, () -> service.importCsvFile(mockMultipartFile),
        "Fail trying to process csv data: Invalid CSV format from file: test.csv");
  }
}