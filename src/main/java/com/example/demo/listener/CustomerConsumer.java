package com.example.demo.listener;

import com.example.demo.dto.CustomerRequest;
import com.example.demo.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerConsumer {

    private final RestTemplate restTemplate;

    public CustomerConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CUSTOMER_REGISTER)
    public void processCustomerRegistration(CustomerRequest customerRequest) {

        String url = "https://apis.codante.io/api/register-user/register";

        try {
            restTemplate.postForEntity(url, customerRequest, String.class);
            System.out.println("Cliente registrado na API externa com sucesso: " + customerRequest.getEmail());
        } catch (Exception e) {
            System.err.println("Erro ao registrar cliente externamente: " + e.getMessage());
            // Aqui o RabbitMQ pode reentregar a mensagem ou mandar para uma DLQ (Dead Letter Queue)
        }
    }
}