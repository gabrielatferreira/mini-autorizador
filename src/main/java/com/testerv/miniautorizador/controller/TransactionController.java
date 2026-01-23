package com.testerv.miniautorizador.controller;

import com.testerv.miniautorizador.dto.TransactionDTO;
import com.testerv.miniautorizador.enums.TransactionStatus;
import com.testerv.miniautorizador.exception.TransactionException;
import com.testerv.miniautorizador.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável por processar as transações financeiras dos cartões.
 * Realiza a autorização de débitos validando saldo, senha e status do cartão.
 */
@Slf4j
@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Endpoints para autorização de transações financeiras")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Autoriza uma transação de débito no cartão informado.
     * * @param dto Dados da transação contendo número do cartão, senha e valor.
     * @return "OK" em caso de sucesso (201) ou o código de erro correspondente (422).
     */
    @Operation(
            summary = "Realizar transação",
            description = "Valida os dados da transação e debita o valor do saldo do cartão. " +
                    "Retorna erro caso o saldo seja insuficiente, senha inválida ou cartão inexistente.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transação autorizada com sucesso",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "OK"))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Transação negada (Saldo insuficiente, Senha inválida ou Cartão inexistente)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(name = "Saldo Insuficiente", value = "SALDO_INSUFICIENTE"),
                                    @ExampleObject(name = "Senha Inválida", value = "SENHA_INVALIDA"),
                                    @ExampleObject(name = "Cartão Inexistente", value = "CARTAO_INEXISTENTE")
                            })
            ),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> authorizeTransaction(@RequestBody TransactionDTO dto) {
        log.info("Iniciando tentativa de transação para o cartão: {}", dto.numeroCartao());

        try {
            transactionService.authorize(dto);
            log.info("Transação autorizada com sucesso. Cartão: {}, Valor: {}",
                    dto.numeroCartao(), dto.valor());
            return ResponseEntity.status(HttpStatus.CREATED).body(TransactionStatus.OK.getDescription());

        } catch (TransactionException e) {
            log.warn("Transação negada para o cartão {}. Motivo: {}",
                    dto.numeroCartao(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }
}
