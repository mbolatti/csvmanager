package com.csvmanager.infrastructure.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class Config {

  public static final String X_DEAD_LETTER_EXCHANGE_HEADER = "x-dead-letter-exchange";
  @Value("${rabbitmq.exchange.name:def-exchange}")
  private String EXCHANGE;

  @Value("${rabbitmq.exchange.dead-letter-exchange:def-dl-exchange}")
  private String DEAD_LETTER_EXCHANGE;

  @Value("${rabbitmq.queue.name-1:def-queue-name-1}")
  private String QUEUE_NAME_1;

  @Value("${rabbitmq.queue.name-2:def-queue-name-2}")
  private String QUEUE_NAME_2;

  @Value("${rabbitmq.queue.name-3:def-queue-name-3}")
  private String QUEUE_NAME_3;

  @Value("${rabbitmq.queue.dead-letter-queue:def-dead-letter-queue}")
  private String QUEUE_DEAD_LETTER;

  @Value("${rabbitmq.routing.key1:def-routing-key1}")
  private String ROUTING_KEY_1;

  @Value("${rabbitmq.routing.key2:def-routing-key2}")
  private String ROUTING_KEY_2;

  @Value("${rabbitmq.routing.key3:def-routing-key3}")
  private String ROUTING_KEY_3;

  @Bean
  public Queue queue1() {
    return QueueBuilder
        .durable(QUEUE_NAME_1)
        /*.autoDelete(false)*/
        .withArgument(X_DEAD_LETTER_EXCHANGE_HEADER, this.deadLetterExchange())
        .build();
  }

  @Bean
  public Queue queue2() {
    return QueueBuilder
        .durable(QUEUE_NAME_2)
        /*.autoDelete()*/
        .withArgument(X_DEAD_LETTER_EXCHANGE_HEADER, this.deadLetterExchange())
        .build();
  }

  @Bean
  public Queue queue3() {
    return QueueBuilder
        .durable(QUEUE_NAME_3)
        /*.autoDelete()*/
        .withArgument(X_DEAD_LETTER_EXCHANGE_HEADER, this.deadLetterExchange())
        .build();
  }

  @Bean
  public DirectExchange deadLetterExchange() {
    return new DirectExchange(DEAD_LETTER_EXCHANGE);
  }

  @Bean
  public Queue deadLetterQueue() {
    return QueueBuilder
        .durable(QUEUE_DEAD_LETTER)
        /*.autoDelete()*/
        .withArgument(X_DEAD_LETTER_EXCHANGE_HEADER, this.deadLetterExchange())
        .build();
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(EXCHANGE);
  }

  @Bean
  public Binding binding1(Queue queue1, TopicExchange exchange) {
    return BindingBuilder.bind(queue1()).to(exchange).with(ROUTING_KEY_1);
  }

  @Bean
  public Binding binding2(Queue queue2, TopicExchange exchange) {
    return BindingBuilder.bind(queue2()).to(exchange).with(ROUTING_KEY_2);
  }

  @Bean
  public Binding binding3(Queue queue3, TopicExchange exchange) {
    return BindingBuilder.bind(queue3()).to(exchange).with(ROUTING_KEY_3);
  }

  @Bean
  public Binding bindingDLQ(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
    return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dlq");
  }
}
