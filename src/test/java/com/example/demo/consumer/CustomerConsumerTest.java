package com.example.demo.consumer;

import com.example.demo.dto.CustomerRequest;
import com.example.demo.listener.CustomerConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerConsumerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CustomerConsumer customerConsumer;

    private CustomerRequest customerRequestValido;
    private final String urlEsperada = "https://apis.codante.io/api/register-user/register";

    @BeforeEach
    void setUp() {
        customerRequestValido = new CustomerRequest();
        customerRequestValido.setName("Rafael Ramos");
        customerRequestValido.setEmail("rafael@test.com");
    }

    @Test
    @DisplayName("Cenário 1: Deve enviar dados para a API externa com sucesso ao consumir mensagem")
    void deveEnviarDadosParaApiExternaComSucesso() {
        // Arrange: Simula uma resposta 200 OK do RestTemplate
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Sucesso", HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq(urlEsperada),
                eq(customerRequestValido),
                eq(String.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertDoesNotThrow(() -> customerConsumer.processCustomerRegistration(customerRequestValido));

        // Verify: Garante que a requisição HTTP POST foi realizada exatamente 1 vez com a URL e o payload corretos
        verify(restTemplate, times(1)).postForEntity(
                eq(urlEsperada),
                eq(customerRequestValido),
                eq(String.class)
        );
    }

    @Test
    @DisplayName("Cenário 2: Deve capturar exceção no catch sem quebrar a aplicação quando a API externa falhar")
    void deveTratarExcecaoQuandoApiExternaRetornarErro() {
        // Arrange: Simula uma falha de conexão ou erro HTTP (ex: 500 Internal Server Error)
        when(restTemplate.postForEntity(
                eq(urlEsperada),
                any(CustomerRequest.class),
                eq(String.class)
        )).thenThrow(new RestClientException("Erro ao conectar no servidor remoto"));

        // Act & Assert: Como o seu código possui um bloco try-catch, a exceção NÃO deve subir (o método não estoura erro)
        assertDoesNotThrow(() -> customerConsumer.processCustomerRegistration(customerRequestValido));

        // Verify: Confirma que o RestTemplate tentou fazer a chamada mesmo ocorrendo o erro
        verify(restTemplate, times(1)).postForEntity(
                eq(urlEsperada),
                any(CustomerRequest.class),
                eq(String.class)
        );
    }
}