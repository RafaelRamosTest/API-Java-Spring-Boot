package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerRequest {
    private String name;
    private String email;
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
            message = "A senha deve conter letra maiúscula, minúscula, número e símbolo."
    )
    private String password;
    @NotBlank(message = "A confirmação de senha é obrigatória.")
    private String password_confirmation;
    private String cpf;
    private String phone;
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 12345-678")
    private String zipcode;
    private String address;
    private String city;
    @AssertTrue(message = "Você deve aceitar os termos para continuar.")
    private boolean terms;

    // getters e setters

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }
    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZipcode() {
        return zipcode;
    }
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public boolean isTerms() {
        return terms;
    }
    public void setTerms(boolean terms) {
        this.terms = terms;
    }

    @AssertTrue(message = "A senha e a confirmação devem ser iguais.")
    @JsonIgnore
    public boolean isPasswordMatching() {
        if (password == null || password_confirmation == null) {
            return false;
        }
        return password.equals(password_confirmation);
    }
}

