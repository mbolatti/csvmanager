import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.csvmanager.domain.jpa.PersonalData;
import com.csvmanager.domain.port.in.dto.Line;
import com.csvmanager.domain.port.out.CsvParser;
import com.csvmanager.domain.port.out.PersonalDataPort;
import com.csvmanager.service.CsvProcessingService;
import com.csvmanager.service.dto.ProcessResultDto;
import com.csvmanager.service.exception.CsvFileFormatException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;


public class CsvProcessingServiceTest {

//  private final PersonalDataPort personalDataPort = mock(PersonalDataPort.class);
//  private final CsvParser csvParser = mock(CsvParser.class);
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
    //create an valida csv file
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

    // Configurar el mock
    LocalDate localDate = LocalDate.now();
    PersonalData personalData = new PersonalData(1L, "test", localDate,"Name1", "Surname19", "23/04/2001", "Naples", "CLTMNDS98L23F600Z");

    CompletableFuture<ProcessResultDto> future = CompletableFuture.supplyAsync(() -> {
      ProcessResultDto result = new ProcessResultDto();
      return result;
    });

    doNothing().when(personalDataPort).save(any(PersonalData.class));

    // Configurar mocks
    /*List<Line> lines = csvProcessingService.parse(mockMultipartFile.getInputStream(), true);
    when(csvParser.parse(any(), anyBoolean())).thenReturn(lines);
    when(s.extractAndSaveLine(any(), any(UUID.class), any(LocalDate.class), any(ProcessResultDto.class)))
        .thenReturn(future);
    doNothing().when(personalDataPort).save(any(PersonalData.class));

    // Ejecutar la prueba
    CsvProcessingService service = new CsvProcessingService(personalDataPort, csvParser);
    ProcessResultDto result = (ProcessResultDto) service.processCsv(mockMultipartFile);

    // Verificar el resultado
    assertEquals(1, result.getOk().getChargedList().size());
    assertTrue(result.getWrong().isEmpty());*/

    CSVRecord record = mock(CSVRecord.class);
    when(csvParser.parse(any(), anyBoolean())).thenReturn(List.of(line));

    /*when(csvProcessingService.extractAndSaveLine(any(), any(UUID.class), any(LocalDate.class), any(ProcessResultDto.class)))
        .thenReturn(future);*/

    ProcessResultDto result = (ProcessResultDto) csvProcessingService.processCsv(mockMultipartFile);

    assertNotNull(result);
    assertEquals(1, result.getOk().getChargedList().size());
    assertEquals(0, result.getWrong().size());
    verify(personalDataPort, times(1)).save(any(PersonalData.class));

    assertEquals("nome='Name1', cognome='Surname19', DataDiNascita='23/04/2001', città='Naples', codiceFiscale='BLAJTN12L23F600Z'", removeUUID(result.getOk().getChargedList().get(0), ","));
  }
/*
  @Test
  @Timeout(value = 5)
  @Transactional
  public void shouldnotProcessInvalidCsvLine() throws IOException {
    String csvContent = "Name1,Surname19,23/04/2001,Naples,BLAJTN12L23F600";
    Map<Integer, String> row = new HashMap<>();
    row.put(0, "Name1");
    row.put(1, "Surname19");
    row.put(2, "23/04/2001");
    row.put(3, "Naples");
    row.put(4, "BLAJTN12L23F600");
    Line line = new Line(1L,row);
    MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", "test.csv", "text/csv",
            csvContent.getBytes(StandardCharsets.UTF_8));

    doNothing().when(personalDataPort).save(any(PersonalData.class));
    when(csvParser.parse(eq(mockMultipartFile.getInputStream()), anyBoolean())).thenReturn(List.of(line));

    //ProcessResultDto result = (ProcessResultDto) csvProcessingService.processCsv(mockMultipartFile);
    *//*CompletableFuture<ProcessResultDto> resultFuture = csvProcessingService.processCsv(mockMultipartFile);

    // Esperar a que todos los CompletableFuture terminen
    resultFuture.get();

    ProcessResultDto result = resultFuture.get();*//*


    assertNotNull(result);
    assertEquals(0, result.getOk().getChargedList().size());
    assertEquals(1, result.getWrong().size());
    verify(personalDataPort, times(0)).save(any(PersonalData.class));

    assertEquals(1L, result.getWrong().get(0).getLineNumber());
    assertEquals("nome='Name1', cognome='Surname19', DataDiNascita='23/04/2001', città='Naples', codiceFiscale='BLAJTN12L23F600'", removeUUID(result.getWrong().get(0).getContent(), ","));
    assertEquals("Invalid fiscal code", result.getWrong().get(0).getErrors().get(0));
  }*/



  @Test
  public void shouldHandleInvalidCsvContentFormat() throws IOException {
    String csvContent = "invalid format";
    MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", "test.csv", "text/csv",
            csvContent.getBytes(StandardCharsets.UTF_8));

    // Configurar mocks
    when(csvParser.parse(any(), anyBoolean())).thenThrow(new IOException("Invalid CSV format"));

    // Ejecutar la prueba y verificar la excepción
    CsvProcessingService service = new CsvProcessingService(personalDataPort, csvParser);
    assertThrows(CsvFileFormatException.class, () -> service.processCsv(mockMultipartFile),
        "Fail trying to process csv data: Invalid CSV format from file: test.csv");
  }

  /*evitar el caso de prueba de duplicados
  errores en la persistencia
  lineas invalidas*/


  private String removeUUID(String originalStr, String splitter) {
    int indiceComa = originalStr.indexOf(splitter);
    String cleanStr = originalStr.substring(indiceComa + 1).trim();
    return cleanStr;
  }

}