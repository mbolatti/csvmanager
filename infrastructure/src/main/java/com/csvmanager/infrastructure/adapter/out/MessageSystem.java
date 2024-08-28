package com.csvmanager.infrastructure.adapter.out;

import com.csvmanager.domain.port.out.MessageSystemPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSystem implements MessageSystemPort {
  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchange.name:def-exchange}")
  private String EXCHANGE;

  @Value("${rabbitmq.routing.key1:def-routing-key1}")
  private String ROUTING_KEY_1;

  @Value("${rabbitmq.routing.key2:def-routing-key2}")
  private String ROUTING_KEY_2;

  @Value("${rabbitmq.routing.key3:def-routing-key3}")
  private String ROUTING_KEY_3;

  public MessageSystem(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }


  public void sendMessage1(String message) {
    rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_1, message);
  }

  public void sendMessage2(String message) {
    rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_2, message);
  }

  public void sendMessage3(String message) {
    rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_3, message);
  }

}
