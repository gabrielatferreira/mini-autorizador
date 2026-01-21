package com.testerv.miniautorizador.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<String> handleTransactionException(TransactionException ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }
}
