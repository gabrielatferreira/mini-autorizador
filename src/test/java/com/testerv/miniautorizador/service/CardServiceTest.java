package com.testerv.miniautorizador.service;

import com.testerv.miniautorizador.dto.CardRequestDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private static final String DEFAULT_CARD_NUMBER = "123456789";
    private static final String DEFAULT_PASSWORD = "1234";

    @Test
    @DisplayName("Should create a new card with 500.00 balance")
    void shouldCreateNewCardWithInitialBalance() {
        CardRequestDTO request = new CardRequestDTO(DEFAULT_CARD_NUMBER, DEFAULT_PASSWORD);

        when(cardRepository.existsById(DEFAULT_CARD_NUMBER)).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card createdCard = cardService.create(request);

        assertNotNull(createdCard);
        assertEquals(DEFAULT_CARD_NUMBER, createdCard.getCardNumber());
        assertEquals(0, new BigDecimal("500.00").compareTo(createdCard.getBalance()));
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    @DisplayName("Should throw exception when card already exists")
    void shouldThrowExceptionWhenCardAlreadyExists() {
        CardRequestDTO request = new CardRequestDTO(DEFAULT_CARD_NUMBER, DEFAULT_PASSWORD);

        when(cardRepository.existsById(DEFAULT_CARD_NUMBER)).thenReturn(true);

        TransactionException exception = assertThrows(TransactionException.class, () -> {
            cardService.create(request);
        });

        assertEquals(DEFAULT_CARD_NUMBER, exception.getMessage());

        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    @DisplayName("Should return balance when card exists")
    void shouldReturnBalanceWhenCardExists() {
        Card card = new Card(DEFAULT_CARD_NUMBER, DEFAULT_PASSWORD);

        when(cardRepository.findById(DEFAULT_CARD_NUMBER)).thenReturn(java.util.Optional.of(card));

        BigDecimal balance = cardService.getBalance(DEFAULT_CARD_NUMBER);

        assertEquals(0, new BigDecimal("500.00").compareTo(balance));
    }

    @Test
    @DisplayName("Should throw exception when getting balance of non-existent card")
    void shouldThrowExceptionWhenCardNotFound() {
        when(cardRepository.findById(DEFAULT_CARD_NUMBER)).thenReturn(java.util.Optional.empty());

        assertThrows(TransactionException.class, () -> cardService.getBalance(DEFAULT_CARD_NUMBER));
    }
}
