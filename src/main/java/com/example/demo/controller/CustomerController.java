package com.example.demo.controller;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.CustomerRequest;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRequest customer) {
        return customerService.registerCustomer(customer);
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Customer> getCustomerByCpf(@PathVariable String cpf) {
        return customerService.getByCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
