package com.testerv.miniautorizador.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionDTO(
        @NotBlank
        String numeroCartao,

        @NotBlank
        String senhaCartao,

        @NotNull
        @Min(0)
        BigDecimal valor
) {}
