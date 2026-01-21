package com.testerv.miniautorizador.service;

import com.testerv.miniautorizador.dto.TransactionDTO;
import com.testerv.miniautorizador.exception.TransactionException;
import com.testerv.miniautorizador.model.Card;
import com.testerv.miniautorizador.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final CardRepository cardRepository;

    public TransactionService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional
    public void authorize(TransactionDTO dto) {
        Card card = getCardCheckingExistence(dto.numeroCartao());
        validatePassword(card, dto.senhaCartao());
        validateBalance(card, dto.valor());
        executeDebit(card, dto.valor());
        cardRepository.save(card);
    }

    private Card getCardCheckingExistence(String cardNumber) {
        return cardRepository.findByCardNumberWithLock(cardNumber)
                .orElseThrow(() -> new TransactionException("CARTAO_INEXISTENTE"));
    }

    private void executeDebit(Card card, BigDecimal value) {
        card.setBalance(card.getBalance().subtract(value));
    }

    private void validatePassword(Card card, String providedPassword) {
        boolean isInvalid = !card.getPassword().equals(providedPassword);
        if (isInvalid) throw new TransactionException("SENHA_INVALIDA");
    }

    private void validateBalance(Card card, BigDecimal value) {
        boolean hasNoFunds = card.getBalance().compareTo(value) < 0;
        if (hasNoFunds) throw new TransactionException("SALDO_INSUFICIENTE");
    }
}
