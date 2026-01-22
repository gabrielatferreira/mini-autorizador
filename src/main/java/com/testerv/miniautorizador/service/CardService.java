package com.testerv.miniautorizador.service;

import com.testerv.miniautorizador.dto.CardRequestDTO;
import com.testerv.miniautorizador.enums.TransactionStatus;
import com.testerv.miniautorizador.exception.TransactionException;
import com.testerv.miniautorizador.model.Card;
import com.testerv.miniautorizador.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Serviço responsável pelas operações de gerenciamento de cartões.
 * <p>
 * Centraliza as regras de negócio para criação de novos cartões e
 * consulta de saldos, interagindo diretamente com a camada de persistência.
 * </p>
 */
@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Cria um novo cartão no sistema com saldo inicial padrão.
     * <p>
     * Verifica se o cartão já existe antes da persistência. Caso exista,
     * lança uma exceção para sinalizar o erro de duplicidade (HTTP 422).
     * </p>
     *
     * @param dto Objeto contendo o número e a senha do novo cartão.
     * @return O objeto {@link Card} persistido com sucesso.
     * @throws TransactionException Se o número do cartão já estiver cadastrado.
     */
    @Transactional
    public Card create(CardRequestDTO dto) {
        if (cardRepository.existsById(dto.numeroCartao())) {
            throw new TransactionException(dto.numeroCartao());
        }

        Card card = new Card(dto.numeroCartao(), dto.senha());
        return cardRepository.save(card);
    }

    /**
     * Consulta o saldo disponível de um cartão específico.
     * * @param cardNumber O número do cartão a ser consultado.
     * @return O saldo atual ({@link BigDecimal}) do cartão encontrado.
     * @throws TransactionException Se o cartão não for encontrado na base de dados.
     */
    public BigDecimal getBalance(String cardNumber) {
        return cardRepository.findById(cardNumber)
                .map(Card::getBalance)
                .orElseThrow(() -> new TransactionException(TransactionStatus.CARTAO_INEXISTENTE.getDescription()));
    }
}
