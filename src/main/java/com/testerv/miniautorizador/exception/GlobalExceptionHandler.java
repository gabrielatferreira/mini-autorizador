package com.testerv.miniautorizador.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manipulador global de exceções para a camada Web.
 * <p>
 * Esta classe utiliza as facilidades do Spring Framework ({@link RestControllerAdvice})
 * para interceptar exceções lançadas em qualquer Controller da aplicação e
 * formatar a resposta HTTP de acordo com os requisitos da especificação.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manipula a exceção lançada quando ocorre uma tentativa de cadastrar um cartão que já existe.
     * <p>
     * De acordo com os requisitos de negócio, quando um cartão já está presente na base de dados,
     * o sistema deve retornar o status HTTP 422 (Unprocessable Entity) contendo os dados do
     * cartão que gerou o conflito.
     * </p>
     *
     * @param e A exceção {@link CardAlreadyExistsException} contendo os detalhes do cartão duplicado.
     * @return Uma {@link ResponseEntity} contendo um mapa com o número do cartão e o status 422.
     */
    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleCardAlreadyExists(CardAlreadyExistsException e) {
        log.info("Cadastro negado: Cartão {} já existe.", e.getCardNumber());
        Map<String, String> response = new LinkedHashMap<>();
        response.put("senha", e.getPassword());
        response.put("numeroCartao", e.getCardNumber());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

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

    /**
     * Manipula a exceção lançada quando um cartão não é encontrado durante a consulta de saldo.
     * <p>
     * Conforme as especificações do desafio, este método intercepta a {@link CardNotFoundException}
     * e retorna o status HTTP 404 (Not Found). O corpo da resposta é explicitamente deixado vazio
     * para cumprir os requisitos de segurança e de protocolo da API.
     * </p>
     *
     * @param e A exceção do tipo {@link CardNotFoundException}.
     * @return Uma {@link ResponseEntity} com status 404 e corpo vazio (Void).
     */
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Void> handleCardNotFoundException(CardNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    /**
     * Tratamento genérico para qualquer exceção não mapeada explicitamente.
     * <p>
     * Este método atua como a última camada de defesa da aplicação (fallback).
     * Ele captura falhas inesperadas — como erros de conectividade com o banco de dados,
     * {@link NullPointerException} ou erros de infraestrutura — garantindo que o sistema
     * não exponha detalhes técnicos sensíveis (stacktraces) no corpo da resposta HTTP.
     * </p>
     * <p>
     * O erro é registrado no log com nível {@code ERROR} para permitir a investigação
     * posterior pelos desenvolvedores, enquanto o cliente recebe uma mensagem amigável
     * com o status HTTP 500 (Internal Server Error).
     * </p>
     *
     * @param e A exceção capturada pelo Spring Framework.
     * @return Uma {@link ResponseEntity} com status 500 e uma mensagem de erro genérica.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Erro inesperado: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado no servidor.");
    }
}
