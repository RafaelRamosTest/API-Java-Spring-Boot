package com.example.demo.service;

import com.example.demo.dto.CustomerRequest;
import com.example.demo.listener.CustomerConsumer;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.config.RabbitMQConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    @Mock
    private CustomerRepository repository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private CustomerService service;
    @Mock
    private CustomerMapper mapper;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CustomerConsumer consumer;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(CustomerRepository.class);
        rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        mapper = Mockito.mock(CustomerMapper.class);

        // Construtor ajustado com RabbitTemplate e CustomerRepository
        // Adicione o Mapper aqui no final caso seu CustomerService receba um ModelMapper/CustomerMapper
        service = new CustomerService(repository, mapper, rabbitTemplate);
    }

    @Test
    void deveSalvarCustomerNoBanco() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Rafael");
        request.setEmail("rafael@email.com");
        request.setPhone("11999999999");
        request.setAddress("Rua das Flores, 123");
        request.setCity("Carapicuíba");
        request.setPassword("senhaSegura123");
        request.setCpf("12345678900");
        request.setZipcode("06320-000");
        request.setTerms(true);

        Customer saved = new Customer(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getAddress(),
                request.getCity(),
                request.getPassword(),
                request.getCpf(),
                request.getZipcode(),
                request.isTerms()
        );
        saved.setId(1L);

        when(repository.save(any(Customer.class))).thenReturn(saved);

        Customer result = service.saveCustomer(request);

        assertNotNull(result);
        assertEquals("Rafael", result.getName());
        assertEquals("11999999999", result.getPhone());
        assertEquals("Carapicuíba", result.getCity());
        assertEquals("12345678900", result.getCpf());
        assertTrue(result.getTerms());

        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void deveRegistrarCustomerEEnviarParaOAmqp() {
        CustomerRequest request = new CustomerRequest();
        request.setName("Rafael");
        request.setEmail("rafael@email.com");
        request.setPhone("11999999999");
        request.setAddress("Rua das Flores, 123");
        request.setCity("Carapicuíba");
        request.setPassword("senhaSegura123");
        request.setCpf("12345678900");
        request.setZipcode("06320-000");
        request.setTerms(true);

        Customer saved = new Customer();
        saved.setId(1L);

        // Mock da gravação no banco
        when(repository.save(any(Customer.class))).thenReturn(saved);

        // Chama o novo método atualizado
        ResponseEntity<String> result = service.registerCustomer(request);

        // Valida o status 202 Accepted
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertEquals("Cadastro em processamento assíncrono.", result.getBody());

        // Verifica se gravou no banco
        verify(repository, times(1)).save(any(Customer.class));

        // Verifica se a mensagem foi disparada no RabbitMQ com os parâmetros corretos
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_CUSTOMER),
                eq(RabbitMQConfig.ROUTING_KEY_CUSTOMER),
                eq(request)
        );
    }

    @Test
    void deveFormatarCpfCorretamente() {
        Optional<Customer> customer = Optional.of(new Customer());
        customer.get().setCpf("123.456.789-00");

        when(repository.findByCpf("123.456.789-00")).thenReturn(customer);

        Optional<Customer> result = service.getByCpf("12345678900");

        assertTrue(result.isPresent());
        assertEquals("123.456.789-00", result.get().getCpf());
    }

    @Test
    void deveLancarExcecaoParaCpfInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.getByCpf("123"); // CPF com menos de 11 dígitos
        });
    }


}