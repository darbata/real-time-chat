package io.darbata.dispatcher;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AmqpConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public JacksonJsonMessageConverter producerJackson2MessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Queue dispatchQueue() {
        return QueueBuilder.durable("dispatch").build();
    }

    @Bean
    public Queue chatQueue() {
        return QueueBuilder.durable("chat").build();
    }
}