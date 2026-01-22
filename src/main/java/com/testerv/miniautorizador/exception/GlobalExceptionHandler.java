package com.testerv.miniautorizador.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manipulador global de exceções para a camada Web.
 * <p>
 * Esta classe utiliza as facilidades do Spring Framework ({@link RestControllerAdvice})
 * para interceptar exceções lançadas em qualquer Controller da aplicação e
 * formatar a resposta HTTP de acordo com os requisitos da especificação.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura e trata exceções do tipo {@link TransactionException}.
     * <p>
     * Conforme os requisitos do desafio, erros de regra de negócio em transações
     * (saldo insuficiente, senha inválida ou cartão inexistente) devem retornar
     * o código de status HTTP 422 (Unprocessable Entity) contendo a mensagem
     * específica do erro no corpo da resposta.
     * </p>
     *
     * @param ex A exceção capturada contendo a mensagem de erro de negócio.
     * @return {@link ResponseEntity} com status 422 e a mensagem detalhada.
     */
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<String> handleTransactionException(TransactionException ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }
}
