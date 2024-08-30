package com.csvmanager.application.service;

import com.csvmanager.domain.port.out.MessageSystemPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Messaging {

  private final MessageSystemPort messageSystem;

  public Messaging(MessageSystemPort messageSystem) {
    this.messageSystem = messageSystem;
  }

  public void sendMessage1(String message) {
    log.info("Sending message to start processing the saved lines: {}", message);
    messageSystem.sendMessage1(message);
  }
}
