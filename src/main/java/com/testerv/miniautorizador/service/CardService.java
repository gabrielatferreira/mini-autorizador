package com.testerv.miniautorizador.service;

import com.testerv.miniautorizador.dto.CardRequestDTO;
import com.testerv.miniautorizador.exception.TransactionException;
import com.testerv.miniautorizador.model.Card;
import com.testerv.miniautorizador.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional
    public Card create(CardRequestDTO dto) {
        if (cardRepository.existsById(dto.numeroCartao())) {
            throw new TransactionException(dto.numeroCartao());
        }

        Card card = new Card(dto.numeroCartao(), dto.senha());
        return cardRepository.save(card);
    }

    public BigDecimal getBalance(String cardNumber) {
        return cardRepository.findById(cardNumber)
                .map(Card::getBalance)
                .orElseThrow(() -> new TransactionException("CARTAO_INEXISTENTE"));
    }
}
