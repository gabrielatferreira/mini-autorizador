package com.testerv.miniautorizador.controller;

import com.testerv.miniautorizador.dto.CardRequestDTO;
import com.testerv.miniautorizador.dto.CardResponseDTO;
import com.testerv.miniautorizador.model.Card;
import com.testerv.miniautorizador.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Cria um novo cartão com saldo inicial de 500.00.
 * * @param request DTO contendo o número do cartão e a senha desejada.
 * @return ResponseEntity com os dados do cartão criado ou erro de duplicidade.
 */
@Slf4j
@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
@Tag(name = "Cartões", description = "Endpoints para gerenciamento de cartões")
@SecurityRequirement(name = "basicAuth")
public class CardController {

    private final CardService cardService;

    @Operation(
            summary = "Criar novo cartão",
            description = "Cria um novo cartão com saldo inicial de 500.00. Se o cartão já existir, retorna erro 422.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Criação com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardRequestDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Caso o cartão já exista",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardRequestDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Erro de autenticação - Credenciais inválidas"
            )
    })
    @PostMapping
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody CardRequestDTO dto) {
        Card newCard = cardService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(CardResponseDTO.fromEntity(newCard));
    }

    @Operation(
            summary = "Obter saldo do cartão",
            description = "Retorna o saldo atual de um cartão existente.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação")
    })
    @GetMapping("/{cardNumber}")
    public ResponseEntity<BigDecimal> getBalance(
            @Parameter(description = "Número do cartão com 16 dígitos", required = true)
            @PathVariable String cardNumber) {
        BigDecimal balance = cardService.getBalance(cardNumber);
        return ResponseEntity.ok(balance);
    }
}
