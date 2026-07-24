package com.example.demo.exception;

import com.example.demo.dto.ErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomerExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleCustomerErrors(HttpClientErrorException ex,
                                                  HttpServletRequest request) {
        int statusCode = ex.getStatusCode().value();

        if (statusCode == 420 || statusCode == 422) {
            String rawBody = ex.getResponseBodyAsString();
            String message = "";

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(rawBody);

                if (node.has("message")) {
                    message = node.get("message").asText();

                    // mapa de traduções simples
                    Map<String, String> traducoes = Map.of(
                            "city", "cidade",
                            "address", "endereço"
                    );

                    for (Map.Entry<String, String> entry : traducoes.entrySet()) {
                        message = message.replace(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception parseEx) {
                message = rawBody;
            }

            return ResponseEntity.status(statusCode)
                    .body(new ErrorResponse(message));
        }

        // Outros erros de negócio (400, 409 etc.)
        if (statusCode == HttpStatus.BAD_REQUEST.value()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            "Dados inválidos para cadastro de cliente",
                            request.getRequestURI()
                    ));
        }

        if (statusCode == HttpStatus.CONFLICT.value()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            HttpStatus.CONFLICT.value(),
                            HttpStatus.CONFLICT.getReasonPhrase(),
                            "E-mail já cadastrado",
                            request.getRequestURI()
                    ));
        }

        // fallback genérico
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(
                        statusCode,
                        ex.getStatusText(),
                        ex.getResponseBodyAsString(),
                        request.getRequestURI()
                ));
    }

    // 👇 Novo handler para erros de validação do CustomerRequest
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Erro de validação");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (message.contains("customer_email_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "E-mail já cadastrado"));
        } else if (message.contains("customer_cpf_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "CPF já cadastrado"));
        } else if (message.contains("customer_phone_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Telefone já cadastrado"));
        } else if (message.contains("customer_zipcode_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "CEP já cadastrado"));
        } else if (message.contains("customer_name_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Nome já cadastrado"));
        } else if (message.contains("customer_password_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Senha já cadastrada"));
        } else if (message.contains("customer_city_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Cidade já cadastrada"));
        } else if (message.contains("customer_address_key")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Endereço já cadastrado"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Violação de integridade de dados"));
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCpf(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "Invalid CPF");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(body);
    }
}
