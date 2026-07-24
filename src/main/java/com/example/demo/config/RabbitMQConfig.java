package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_CUSTOMER_REGISTER = "customer.register.queue";
    public static final String EXCHANGE_CUSTOMER = "customer.exchange";
    public static final String ROUTING_KEY_CUSTOMER = "customer.routing.key";

    @Bean
    public Queue customerQueue() {
        return new Queue(QUEUE_CUSTOMER_REGISTER, true);
    }

    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange(EXCHANGE_CUSTOMER);
    }

    @Bean
    public Binding customerBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_CUSTOMER);
    }

    // Converte os objetos Java para JSON automaticamente ao enviar para a fila
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true); // Força a auto-inicialização das filas/exchanges
        rabbitAdmin.initialize();
        return rabbitAdmin;
    }
}