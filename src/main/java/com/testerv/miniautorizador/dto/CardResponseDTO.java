package com.testerv.miniautorizador.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.testerv.miniautorizador.model.Card;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CardResponseDTO(String numeroCartao, BigDecimal saldo) {

    public static CardResponseDTO fromEntity(Card card) {
        return new CardResponseDTO(card.getCardNumber(), card.getBalance());
    }
}
