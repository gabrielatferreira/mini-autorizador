package com.testerv.miniautorizador.exception;

import lombok.Getter;

/**
 * Exceção lançada quando ocorre uma tentativa de criação de um cartão que já possui
 * um registro correspondente na base de dados.
 * <p>
 * Esta classe estende {@link TransactionException} para manter a hierarquia de erros
 * de negócio, mas carrega adicionalmente o número do cartão e a senha para permitir
 * que o Handler de exceções retorne o corpo da requisição original conforme exigido
 * pelos requisitos do desafio (HTTP 422).
 * </p>
 */
@Getter
public class CardAlreadyExistsException extends TransactionException {
    private final String cardNumber;
    private final String password;

    public CardAlreadyExistsException(String cardNumber, String password) {
        super(cardNumber);
        this.cardNumber = cardNumber;
        this.password = password;
    }
}
