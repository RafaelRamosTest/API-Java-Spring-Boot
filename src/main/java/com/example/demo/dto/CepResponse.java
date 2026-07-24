package com.example.demo.dto;

public class CepResponse {
    private String cep;
    private String state;
    private String city;
    private String neighborhood;
    private String street;
    private String service;

    private String mensagem;

    public CepResponse() {}

    public CepResponse(String mensagem) {
        this.mensagem = mensagem;
    }
    public CepResponse(String cep, String serviçoDeCepIndisponível) {}

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}