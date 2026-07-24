package com.example.demo.dto;

import jakarta.validation.constraints.Size;

public class CepRequest {

    @Size(min = 8, max = 8, message = "CEP deve ter exatamente 8 dígitos")
    private String cep;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
