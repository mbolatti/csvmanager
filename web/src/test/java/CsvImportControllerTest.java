import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.csvmanager.config.ApplicationConfig;
import com.csvmanager.controller.CsvImportController;
import com.csvmanager.domain.port.in.ProcessCsvUseCase;
import com.csvmanager.infrastructure.repository.PersonalDataRepository;
import com.csvmanager.service.dto.PersonalDataDto;
import com.csvmanager.service.dto.ProcessResultDto;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {CsvImportController.class, ApplicationConfig.class, TestSecurityConfig.class})
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude= {SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class})
public class CsvImportControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProcessCsvUseCase processCsvUseCase;

  @MockBean
  private PersonalDataRepository personalDataRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testImportCsv_success() throws Exception {
    ProcessResultDto mockResult = new ProcessResultDto();
    when(processCsvUseCase.processCsv(any(MockMultipartFile.class))).thenReturn(mockResult);

    MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv",
        "name,surname,date,city,code\n".getBytes());

    mockMvc.perform(multipart("/api/v1/private/data/imports")
            .file(file)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  public void testGetAllImportedCSV_success() throws Exception {
    List<PersonalDataDto> mockResult = new ArrayList<>();
    when(processCsvUseCase.returnAll()).thenReturn(mockResult);

    mockMvc.perform(get("/api/v1/private/data/imports")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]")); // Asegúrate de reemplazar con el JSON real esperado
  }

  @Test
  public void testExportCsv_success() throws Exception {
    List<PersonalDataDto> personalDataDtos = new ArrayList<>();
    LocalDate importDate = LocalDate.now();
    personalDataDtos.add(
        new PersonalDataDto(1L, "import1", importDate, "Name1", "Surname1", "01/01/2000", "City1",
            "Code1"));
    personalDataDtos.add(
        new PersonalDataDto(3L, "import2", importDate, "Name2", "Surname2", "02/02/2001", "City2",
            "Code2"));

    byte[] EXPECTED_CSV_CONTENT = "Name1,Surname1,01/01/2000,City1,Code1\r\nName2,Surname2,02/02/2001,City2,Code2\r\n".getBytes();

    doAnswer(invocation -> {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try (CSVPrinter printer = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT)) {
        for (PersonalDataDto personalDataDto : personalDataDtos) {
          printer.printRecord(personalDataDto.getName(), personalDataDto.getLastName(),
              personalDataDto.getBirthDate(), personalDataDto.getCity(),
              personalDataDto.getFiscalCode());
        }
      }
      return outputStream;
    }).when(processCsvUseCase).writePersonalDataToCsv(any(ByteArrayOutputStream.class));

    mockMvc.perform(get("/api/v1/private/data/exports"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
        .andExpect(
            header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"export.csv\""))
        .andExpect(content().bytes(EXPECTED_CSV_CONTENT));
  }

}
