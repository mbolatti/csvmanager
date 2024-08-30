package com.csvmanager.infrastructure.rabbit;

import java.util.concurrent.CountDownLatch;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListeners {


  private CountDownLatch latch = new CountDownLatch(1);

  @RabbitListener(queues = "#{config.deadLetterQueue}")
  public void receiveDeadLetter(String message) {
    System.out.println("Received dead letter <" + message + ">");
    latch.countDown();
  }

  public CountDownLatch getLatch() {
    return latch;
  }

}