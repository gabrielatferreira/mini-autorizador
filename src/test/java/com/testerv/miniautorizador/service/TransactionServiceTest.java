package com.testerv.miniautorizador.service;

import com.testerv.miniautorizador.dto.TransactionDTO;
import com.testerv.miniautorizador.exception.TransactionException;
import com.testerv.miniautorizador.model.Card;
import com.testerv.miniautorizador.repository.CardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransactionService transactionService;

    private static final String DEFAULT_CARD_NUMBER = "123456789";
    private static final String DEFAULT_PASSWORD = "1234";

    @Test
    @DisplayName("Should authorize transaction successfully and update balance")
    void shouldAuthorizeTransactionSuccessfully() {
        BigDecimal initialBalance = new BigDecimal("500.00");
        Card existingCard = new Card(DEFAULT_CARD_NUMBER, DEFAULT_PASSWORD);
        existingCard.setBalance(initialBalance);

        BigDecimal transactionValue = new BigDecimal("100.00");
        TransactionDTO transactionRequest = new TransactionDTO(DEFAULT_CARD_NUMBER, DEFAULT_PASSWORD, transactionValue);

        when(cardRepository.findByCardNumberWithLock(DEFAULT_CARD_NUMBER)).thenReturn(Optional.of(existingCard));

        assertDoesNotThrow(() -> transactionService.authorize(transactionRequest));

        BigDecimal expectedBalance = new BigDecimal("400.00");
        assertEquals(0, expectedBalance.compareTo(existingCard.getBalance()), "Balance should be 400.00");

        verify(cardRepository, times(1)).save(existingCard);
    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        String wrongPassword = "9999";

        Card existingCard = new Card(DEFAULT_CARD_NUMBER, DEFAULT_PASSWORD);

        TransactionDTO dtoWithWrongPassword = new TransactionDTO(DEFAULT_CARD_NUMBER, wrongPassword, new BigDecimal("10.00"));

        when(cardRepository.findByCardNumberWithLock(DEFAULT_CARD_NUMBER)).thenReturn(Optional.of(existingCard));

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.authorize(dtoWithWrongPassword);
        });

        assertEquals("SENHA_INVALIDA", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when balance is insufficient")
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        BigDecimal initialBalance = new BigDecimal("10.00");
        Card existingCard = new Card(DEFAULT_CARD_NUMBER, DEFAULT_PASSWORD);
        existingCard.setBalance(initialBalance);

        TransactionDTO expensiveTransaction = new TransactionDTO(DEFAULT_CARD_NUMBER, DEFAULT_PASSWORD, new BigDecimal("100.00"));

        when(cardRepository.findByCardNumberWithLock(DEFAULT_CARD_NUMBER)).thenReturn(Optional.of(existingCard));

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.authorize(expensiveTransaction);
        });

        assertEquals("SALDO_INSUFICIENTE", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when card does not exist")
    void shouldThrowExceptionWhenCardDoesNotExist() {
        String cardNumber = "999888777";
        TransactionDTO dto = new TransactionDTO(cardNumber, DEFAULT_PASSWORD, new BigDecimal("10.00"));

        when(cardRepository.findByCardNumberWithLock(cardNumber)).thenReturn(Optional.empty());

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            transactionService.authorize(dto);
        });

        assertEquals("CARTAO_INEXISTENTE", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }
}