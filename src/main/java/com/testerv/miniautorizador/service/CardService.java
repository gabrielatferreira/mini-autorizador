package com.testerv.miniautorizador.service;

import com.testerv.miniautorizador.dto.CardRequestDTO;
import com.testerv.miniautorizador.enums.TransactionStatus;
import com.testerv.miniautorizador.exception.CardAlreadyExistsException;
import com.testerv.miniautorizador.exception.CardNotFoundException;
import com.testerv.miniautorizador.exception.TransactionException;
import com.testerv.miniautorizador.model.Card;
import com.testerv.miniautorizador.repository.CardRepository;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            log.warn("Falha ao criar cartão: {} já existe no sistema.", dto.numeroCartao());
            throw new CardAlreadyExistsException(dto.numeroCartao(), dto.senha());
        }

        Card card = new Card(dto.numeroCartao(), dto.senha());
        log.info("Novo cartão criado com sucesso: {}", dto.numeroCartao());
        return cardRepository.save(card);
    }

    /**
     * Consulta o saldo disponível de um cartão específico.
     * * @param cardNumber O número do cartão a ser consultado.
     * @return O saldo atual ({@link BigDecimal}) do cartão encontrado.
     * @throws TransactionException Se o cartão não for encontrado na base de dados.
     */
    public BigDecimal getBalance(String cardNumber) {
        log.debug("Consultando saldo para o cartão: {}", cardNumber);

        return cardRepository.findById(cardNumber)
                .map(card -> {
                    log.info("Saldo consultado para o cartão {}: {}", cardNumber, card.getBalance());
                    return card.getBalance();
                })
                .orElseThrow(() -> {
                    log.warn("Cartão inexistente: {}", cardNumber);
                    return new CardNotFoundException();
                });
    }
}
