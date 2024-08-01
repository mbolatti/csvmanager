package com.csvmanager.config;


import com.csvmanager.domain.port.in.ProcessCsvUseCase;
import com.csvmanager.domain.port.out.CsvParser;
import com.csvmanager.domain.port.out.PersonalDataPort;
import com.csvmanager.infrastructure.adapter.out.DatabaseAdapter;
import com.csvmanager.infrastructure.csv.ApacheCommonsCsvParser;
import com.csvmanager.infrastructure.csv.CustomCsvParser;
import com.csvmanager.infrastructure.repository.PersonalDataRepository;
import com.csvmanager.service.CsvProcessingService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ApplicationConfig {

  @Bean
  public PersonalDataPort personalDataPort(PersonalDataRepository personalDataRepository) {
    return new DatabaseAdapter(personalDataRepository);
  }

  @Bean
  public ProcessCsvUseCase processCsvUseCase(PersonalDataPort personalDataPort,
      CsvParser csvParser) {
    return new CsvProcessingService(personalDataPort, csvParser);
  }

  @Bean
  @Primary
  @ConditionalOnProperty(name = "application.csv.parser", havingValue = "apache-commons-csv")
  public CsvParser apacheCsvParser() {
    return new ApacheCommonsCsvParser();
  }

  @Bean
  @ConditionalOnMissingBean(ApacheCommonsCsvParser.class)
  public CsvParser defaultCsvParser() {
    return new CustomCsvParser();
  }


  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("csvmanager-");
    executor.initialize();
    executor.setRejectedExecutionHandler(new CallerRunsPolicy() {
      @Override
      public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println(String.format("Task rejected: %s", r.toString()));
        super.rejectedExecution(r, executor);
      }
    });
    return executor;
  }

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI().addSecurityItem(new SecurityRequirement().
            addList("Bearer Authentication"))
        .components(new Components().addSecuritySchemes
            ("Bearer Authentication", createAPIKeyScheme()))
        .info(new Info().title("CSV IMPORTER REST API")
            .description("API to import a CSV file")
            .version("0.1").contact(new Contact().name("Martin Bolatti"))
            .license(new License().name("License of API")
                .url("API license URL")));
  }


  private SecurityScheme createAPIKeyScheme() {
    return new SecurityScheme().type(SecurityScheme.Type.HTTP)
        .bearerFormat("JWT")
        .scheme("bearer");
  }
}
