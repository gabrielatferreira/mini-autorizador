package com.testerv.miniautorizador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) utilizado para as requisições de criação de novos cartões.
 * <p>
 * Este record utiliza validações do Jakarta Bean Validation para garantir a integridade
 * dos dados recebidos pela API.
 * </p>
 * * @param numeroCartao O número identificador do cartão, contendo exatamente 16 dígitos.
 * @param senha A senha associada ao cartão para autorização de transações.
 */
public record CardRequestDTO(
        @NotBlank(message = "O número do cartão é obrigatório")
        @Size(min = 16, max = 16, message = "O número do cartão deve ter exatamente 16 dígitos")
        String numeroCartao,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {}
