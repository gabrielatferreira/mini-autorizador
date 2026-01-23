package com.testerv.miniautorizador.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para quando o cartão não é encontrado.
 * A anotação @ResponseStatus(HttpStatus.NOT_FOUND) garante que,
 * se não for tratada no Handler, o Spring já retorne 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException() {
        super();
    }
}
