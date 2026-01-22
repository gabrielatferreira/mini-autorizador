package com.testerv.miniautorizador.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representa os possíveis estados e mensagens de retorno de uma operação financeira.
 * <p>
 * Este enum é utilizado para padronizar as respostas da API, garantindo que
 * o autorizador retorne exatamente as Strings esperadas pelos requisitos de negócio.
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    OK("OK"),
    SALDO_INSUFICIENTE("SALDO_INSUFICIENTE"),
    SENHA_INVALIDA("SENHA_INVALIDA"),
    CARTAO_INEXISTENTE("CARTAO_INEXISTENTE");

    /**
     * Descrição textual do status que será enviada no corpo da resposta (Response Body).
     */
    private final String description;
}
