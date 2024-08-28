package com.csvmanager.web.config;


import com.csvmanager.domain.port.in.ProcessCsvUseCase;
import com.csvmanager.domain.port.out.CsvParser;
import com.csvmanager.domain.port.out.FileProcessPort;
import com.csvmanager.domain.port.out.LineDataPort;
import com.csvmanager.domain.port.out.MessageSystemPort;
import com.csvmanager.domain.port.out.PersonalDataPort;
import com.csvmanager.infrastructure.adapter.out.PersonalDataAdapter;
import com.csvmanager.infrastructure.adapter.out.MessageSystem;
import com.csvmanager.infrastructure.csv.ApacheCommonsCsvParser;
import com.csvmanager.infrastructure.csv.CustomCsvParser;
import com.csvmanager.infrastructure.repository.PersonalDataRepository;
import com.csvmanager.application.service.CsvProcessingService;
import com.csvmanager.application.service.CsvQueueProcessingService;
import com.csvmanager.application.service.Messaging;
import com.csvmanager.web.controller.CsvImportController;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
public class ApplicationConfig {

  @Autowired
  private Environment environment;

  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  public PersonalDataPort personalDataPort(PersonalDataRepository personalDataRepository) {
    return new PersonalDataAdapter(personalDataRepository);
  }

  @Bean
  public MessageSystemPort messageSystemPort(RabbitTemplate rabbitTemplate) {
    return new MessageSystem(rabbitTemplate);
  }

  @Bean
  public Messaging messaging(MessageSystemPort messageSystemPort) {
    return new Messaging(messageSystemPort);
  }

  @Bean
  @ConditionalOnProperty(name = "application.processingModel", havingValue = "sync")
  @ConditionalOnMissingBean(CsvQueueProcessingService.class)
  public ProcessCsvUseCase syncProcessCsvUseCase(PersonalDataPort personalDataPort,
      CsvParser csvParser, Messaging messaging) {
    return new CsvProcessingService(personalDataPort, csvParser, messaging);
  }

  @Bean
  @ConditionalOnProperty(name = "application.processingModel", havingValue = "async")
  @ConditionalOnMissingBean(CsvProcessingService.class)
  public ProcessCsvUseCase asyncProcessCsvUseCase(PersonalDataPort personalDataPort,
      FileProcessPort fileProcessPort,
      LineDataPort lineDataPort,
      CsvParser csvParser, Messaging messaging) {
    return new CsvQueueProcessingService(personalDataPort, fileProcessPort, lineDataPort, csvParser,
        messaging);
  }

  @Bean
  @Lazy
  public CsvImportController csvImportController(ProcessCsvUseCase processCsvUseCase) {
    /*String processingModel = environment.getProperty("application.processingModel");
    String qualifier = processingModel + "ProcessCsvUseCase";
    log.info("Injecting bean with qualifier: {}", qualifier);*/

    return new CsvImportController(processCsvUseCase);
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
