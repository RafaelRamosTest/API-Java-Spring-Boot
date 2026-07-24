package com.example.demo.controller;

import com.example.demo.dto.CustomerRequest;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService service;

    @Test
    void deveCadastrarCustomer() throws Exception {
        CustomerRequest request = new CustomerRequest();
        request.setName("Rafael");
        request.setEmail("rafael@email.com");
        request.setCpf("12345678900");
        request.setPhone("11999999999");
        request.setAddress("Rua das Flores, 123");
        request.setCity("Carapicuíba");
        request.setPassword("senhaSegura123@");
        request.setPassword_confirmation("senhaSegura123@");
        request.setZipcode("06320-000");
        request.setTerms(true);

        // Configura o mock da service
        Mockito.when(service.registerCustomer(any(CustomerRequest.class)))
                .thenReturn(ResponseEntity.ok("Sucesso"));

        // Serializa o objeto para JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        // Executa o teste com MockMvc
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso"));
    }


    @Test
    void deveBuscarCustomerPorCpf() throws Exception {
        Customer customer = new Customer("Rafael", "rafael@email.com",
                "11999999999", "Rua das Flores, 123", "Carapicuíba",
                "senhaSegura123", "123.456.789-00", "06320-000", true);

        Mockito.when(service.getByCpf("12345678900"))
                .thenReturn(Optional.of(customer));

        mockMvc.perform(get("/customer/12345678900"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rafael"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"));
    }
}

