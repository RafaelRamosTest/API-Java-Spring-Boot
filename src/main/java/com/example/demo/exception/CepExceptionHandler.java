package com.example.demo.exception;

import com.example.demo.dto.ErrorResponse;
//import com.example.demo.dto.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class CepExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleCepNotFound(HttpClientErrorException ex,
                                                           HttpServletRequest request) {
        // Erro de negócio → mantém ErrorResponse completo
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "CEP não encontrado",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ErrorResponse> handleCepBadRequest(HttpClientErrorException ex,
                                                                       HttpServletRequest request) {
        // Erro de validação → retorna apenas a mensagem
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("CEP inválido"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleCepConstraintViolation(ConstraintViolationException ex,
                                                                                HttpServletRequest request) {
        // Erro de validação → retorna apenas a mensagem da constraint
        String message = ex.getConstraintViolations().iterator().next().getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }
}
