package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.CustomerRequest;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final RabbitTemplate rabbitTemplate; // 👈 Injetado para a mensageria
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository repository, CustomerMapper mapper, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public ResponseEntity<String> registerCustomer(CustomerRequest customerRequest) {
        saveCustomer(customerRequest);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_CUSTOMER,
                RabbitMQConfig.ROUTING_KEY_CUSTOMER,
                customerRequest
        );

        log.info("Mensagem publicada com sucesso na Exchange [{}] com a RoutingKey [{}]: {}",
                RabbitMQConfig.EXCHANGE_CUSTOMER,
                RabbitMQConfig.ROUTING_KEY_CUSTOMER,
                customerRequest);

        return ResponseEntity.accepted().body("Cadastro em processamento assíncrono.");
    }

    public Customer saveCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer(
                customerRequest.getName(),
                customerRequest.getEmail(),
                customerRequest.getPhone(),
                customerRequest.getAddress(),
                customerRequest.getCity(),
                customerRequest.getPassword(),
                customerRequest.getCpf(),
                customerRequest.getZipcode(),
                customerRequest.isTerms()
        );
        return repository.save(customer);
    }

    public List<CustomerDTO> findAll() {
        return mapper.toDTOList(repository.findAll());
    }

    public Optional<Customer> getByCpf(String cpfSemFormatacao) {
        String cpfFormatado = formatCpf(cpfSemFormatacao);
        return repository.findByCpf(cpfFormatado);
    }

    private String formatCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            throw new IllegalArgumentException("CPF inválido. Deve conter 11 dígitos numéricos.");
        }
        return String.format("%s.%s.%s-%s",
                cpf.substring(0, 3),
                cpf.substring(3, 6),
                cpf.substring(6, 9),
                cpf.substring(9, 11));
    }
}