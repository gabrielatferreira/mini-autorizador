package com.testerv.miniautorizador.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) que transporta os dados necessários para a realização de uma transação.
 * <p>
 * Contém as informações essenciais para identificar o cartão, validar a autenticidade
 * através da senha e processar o débito conforme o valor solicitado.
 * </p>
 * * @param numeroCartao O número identificador do cartão que sofrerá o débito.
 * @param senhaCartao A senha para validação de segurança da transação.
 * @param valor O montante monetário a ser debitado do saldo do cartão (deve ser maior ou igual a zero).
 */
@Schema(description = "Dados para processamento de uma nova transação financeira")
public record TransactionDTO(

        @Schema(
                description = "Número do cartão de 16 dígitos",
                example = "6549873025634501",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "O número do cartão é obrigatório")
        @Pattern(regexp = "\\d{16}", message = "O número do cartão deve conter exatamente 16 dígitos numéricos")
        String numeroCartao,

        @Schema(
                description = "Senha do cartão",
                example = "1234",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "A senha do cartão é obrigatória")
        String senhaCartao,

        @Schema(
                description = "Valor da transação",
                example = "10.50",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "O valor da transação é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor da transação deve ser no mínimo 0.01")
        BigDecimal valor
) {}
