package com.example.demo.service;

import com.example.demo.dto.CepResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class CepService {
    private final RestTemplate restTemplate;

    public CepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String API_URL = "https://brasilapi.com.br/api/cep/v1/{cep}";

    @Retryable(
            value = { ResourceAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public CepResponse buscarCep(String cep) {
        if (cep == null || cep.length() != 8) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "CEP inválido");
        }
        try {
            ResponseEntity<CepResponse> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.GET,
                    null,
                    CepResponse.class,
                    cep
            );

            CepResponse body = response.getBody();
            if (body == null || body.getCep() == null) {
                // Se a API respondeu mas sem corpo válido, tratamos como indisponível
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Resposta inválida");
            }

            return body;

        } catch (HttpClientErrorException e) {
            // Relança para o handler decidir se é 404 ou 400
            throw e;
        } catch (ResourceAccessException e) {
            // Sem conexão → 503
            throw e;
        } catch (HttpServerErrorException e) {
            // Indisponível → 500
            throw e;
        }
    }

    @Recover
    public CepResponse fallback(ResourceAccessException e, String cep) {
        // Esse fallback só será usado se o retry esgotar as tentativas
        throw e; // deixa a exceção subir para o GlobalExceptionHandler
    }
}
