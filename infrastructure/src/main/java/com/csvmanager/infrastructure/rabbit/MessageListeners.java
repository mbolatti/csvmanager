package com.csvmanager.infrastructure.rabbit;

import java.util.concurrent.CountDownLatch;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListeners {


  private CountDownLatch latch = new CountDownLatch(1);

 /* @RabbitListener(queues = "#{config.queue1}")
  public void receive1(String message) {
    // comenzar a procesar las lineas guardadas
    System.out.println("Received queue1 <" + message + ">");
    latch.countDown();

  }*/

  @RabbitListener(queues = "#{config.queue2}")
  public void receive2(String message) {
    System.out.println("Received queue2 <" + message + ">");
    latch.countDown();
  }

  @RabbitListener(queues = "#{config.queue3}")
  public void receive3(String message) {
    throw new RuntimeException("Can't process message: " + message);
    /*System.out.println("Received queue3 <" + message + ">");*/
    /*latch.countDown();*/
  }

  @RabbitListener(queues = "#{config.deadLetterQueue}")
  public void receiveDeadLetter(String message) {
    System.out.println("Received dead letter <" + message + ">");
    latch.countDown();
  }

  public CountDownLatch getLatch() {
    return latch;
  }

}