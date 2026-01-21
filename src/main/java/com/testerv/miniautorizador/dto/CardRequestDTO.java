package com.testerv.miniautorizador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CardRequestDTO(
        @NotBlank
        @Size(min = 16, max = 16)
        String numeroCartao,

        @NotBlank
        String senha
) {}
