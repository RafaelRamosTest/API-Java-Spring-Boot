package com.example.demo.controller;

import com.example.demo.dto.CepResponse;
import com.example.demo.service.CepService;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cep")
@Validated
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<CepResponse> getCep(
            @PathVariable @Valid
            @Size(min = 8, max = 8, message = "CEP deve ter exatamente 8 dígitos")
            String cep) {
        CepResponse response = cepService.buscarCep(cep);
        return ResponseEntity.ok(response);
    }

}
