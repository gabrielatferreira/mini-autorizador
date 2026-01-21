package com.testerv.miniautorizador.controller;

import com.testerv.miniautorizador.dto.TransactionDTO;
import com.testerv.miniautorizador.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String> authorizeTransaction(@RequestBody TransactionDTO dto) {
        transactionService.authorize(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
