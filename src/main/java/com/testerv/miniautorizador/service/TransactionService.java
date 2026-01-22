package com.testerv.miniautorizador.service;

import com.testerv.miniautorizador.dto.TransactionDTO;
import com.testerv.miniautorizador.enums.TransactionStatus;
import com.testerv.miniautorizador.exception.TransactionException;
import com.testerv.miniautorizador.model.Card;
import com.testerv.miniautorizador.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Serviço responsável por orquestrar o fluxo de autorização de transações financeiras.
 * <p>
 * O processo segue uma ordem rigorosa de validação para atender aos requisitos da VR:
 * 1. Existência do cartão;
 * 2. Validação de senha;
 * 3. Verificação de saldo suficiente.
 * </p>
 */
@Service
public class TransactionService {

    private final CardRepository cardRepository;

    public TransactionService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Realiza a autorização de uma transação de débito.
     * <p>
     * O método é anotado com {@link Transactional} para garantir que a operação
     * de débito e persistência ocorra dentro de uma única transação de banco de dados,
     * permitindo rollback automático em caso de falhas.
     * </p>
     *
     * @param dto DTO contendo o número do cartão, senha e valor da transação.
     * @throws TransactionException Caso qualquer regra de negócio seja violada.
     */
    @Transactional
    public void authorize(TransactionDTO dto) {
        Card card = getCardCheckingExistence(dto.numeroCartao());
        validatePassword(card, dto.senhaCartao());
        validateBalance(card, dto.valor());
        executeDebit(card, dto.valor());
        cardRepository.save(card);
    }

    /**
     * Recupera o cartão da base de dados aplicando um Lock Pessimista.
     * * @param cardNumber O número do cartão.
     * @return O objeto {@link Card} recuperado.
     * @throws TransactionException Se o cartão não for encontrado.
     */
    private Card getCardCheckingExistence(String cardNumber) {
        return cardRepository.findByCardNumberWithLock(cardNumber)
                .orElseThrow(() -> new TransactionException(TransactionStatus.CARTAO_INEXISTENTE.getDescription()));
    }

    /**
     * Realiza a dedução do valor no saldo do cartão.
     * * @param card A entidade do cartão.
     * @param value O valor a ser debitado.
     */
    private void executeDebit(Card card, BigDecimal value) {
        card.setBalance(card.getBalance().subtract(value));
    }

    /**
     * Valida se a senha fornecida corresponde à senha do cartão.
     * * @param card A entidade do cartão recuperada.
     * @param providedPassword A senha enviada na requisição.
     * @throws TransactionException Se a senha for inválida.
     */
    private void validatePassword(Card card, String providedPassword) {
        boolean isInvalid = !card.getPassword().equals(providedPassword);
        if (isInvalid) throw new TransactionException(TransactionStatus.SENHA_INVALIDA.getDescription());
    }

    /**
     * Valida se o cartão possui saldo suficiente para a transação.
     * * @param card A entidade do cartão.
     * @param value O valor da transação.
     * @throws TransactionException Se o saldo for insuficiente.
     */
    private void validateBalance(Card card, BigDecimal value) {
        boolean hasNoFunds = card.getBalance().compareTo(value) < 0;
        if (hasNoFunds) throw new TransactionException(TransactionStatus.SALDO_INSUFICIENTE.getDescription());
    }
}
