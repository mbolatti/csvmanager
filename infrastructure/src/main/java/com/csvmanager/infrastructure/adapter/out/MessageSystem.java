package com.csvmanager.infrastructure.adapter.out;

import com.csvmanager.domain.port.out.MessageSystemPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageSystem implements MessageSystemPort {
  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchange.name:def-exchange}")
  private String EXCHANGE;

  @Value("${rabbitmq.routing.key1:def-routing-key1}")
  private String ROUTING_KEY_1;

  public MessageSystem(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void sendMessage1(String message) {
    log.info("Sending message to start processing the saved lines: {}", message);
    rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_1, message);
  }
}
