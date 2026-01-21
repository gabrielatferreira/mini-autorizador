package com.testerv.miniautorizador.controller;

import com.testerv.miniautorizador.dto.CardRequestDTO;
import com.testerv.miniautorizador.dto.CardResponseDTO;
import com.testerv.miniautorizador.exception.TransactionException;
import com.testerv.miniautorizador.model.Card;
import com.testerv.miniautorizador.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody CardRequestDTO dto) {
        try {
            Card newCard = cardService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(CardResponseDTO.fromEntity(newCard));
        } catch (TransactionException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new CardResponseDTO(dto.numeroCartao(), null));
        }
    }

    @GetMapping("/{cardNumber}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String cardNumber) {
        try {
            BigDecimal balance = cardService.getBalance(cardNumber);
            return ResponseEntity.ok(balance);
        } catch (TransactionException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
