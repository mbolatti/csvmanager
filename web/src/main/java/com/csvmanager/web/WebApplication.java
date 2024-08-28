package com.csvmanager.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.csvmanager"})
@EnableJpaRepositories(basePackages = {"com.csvmanager.auth", "com.csvmanager.infrastructure"})
@EntityScan(basePackages = {"com.csvmanager.auth", "com.csvmanager.domain", "com.csvmanager.infrastructure"})
public class WebApplication {
  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }
}