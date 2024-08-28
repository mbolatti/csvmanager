package com.csvmanager.web.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application")
@Data
public class AppProperties {

  private CSV csv;
  private String processingModel;

  @Data
  private static class CSV {

    private String parser;
  }
}
