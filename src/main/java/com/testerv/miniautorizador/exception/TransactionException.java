package com.testerv.miniautorizador.exception;

/**
 * Exceção de negócio lançada quando uma operação de transação ou gerenciamento
 * de cartões não atende aos critérios necessários.
 * <p>
 * Esta exceção é do tipo {@link RuntimeException}, permitindo que o Spring Framework
 * realize o rollback automático de transações de banco de dados quando necessário.
 * As mensagens contidas nesta exceção são mapeadas diretamente para as respostas
 * da API através do {@code GlobalExceptionHandler}.
 * </p>
 */
public class TransactionException extends RuntimeException {

    /**
     * Constrói uma nova exceção com a mensagem de erro específica.
     * * @param message Mensagem que descreve o motivo da falha (ex: SALDO_INSUFICIENTE).
     */
    public TransactionException(String message) {
        super(message);
    }
}
