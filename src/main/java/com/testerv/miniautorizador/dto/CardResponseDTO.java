package com.testerv.miniautorizador.dto;

import com.testerv.miniautorizador.model.Card;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO (Data Transfer Object) utilizado para representar a resposta de operações de criação de cartões.
 * <p>
 * Esta classe foi estruturada para retornar exatamente o formato exigido pelo desafio:
 * primeiro a senha e depois o número do cartão, garantindo a compatibilidade com os testes.
 * </p>
 * * @param senha A senha associada ao cartão.
 * @param numeroCartao O número identificador do cartão.
 */
@Schema(description = "Representação de resposta para criação de cartão")
@JsonPropertyOrder({ "senha", "numeroCartao" })
public record CardResponseDTO(

        @Schema(description = "Senha do cartão", example = "1234")
        String senha,

        @Schema(description = "Número do cartão", example = "6549873025634501")
        String numeroCartao
) {

    /**
     * Método utilitário para converter uma entidade {@link Card} em {@link CardResponseDTO}.
     * Utilizado para retornar os dados após o sucesso na persistência.
     * * @param card A entidade persistida no banco de dados.
     * @return Uma nova instância de CardResponseDTO formatada.
     */
    public static CardResponseDTO fromEntity(Card card) {
        return new CardResponseDTO(card.getPassword(), card.getCardNumber());
    }

    /**
     * Método utilitário para criar uma resposta a partir de dados brutos.
     * Útil para retornar o corpo da resposta em cenários de erro 422 (cartão já existe).
     * * @param numeroCartao Número do cartão.
     * @param senha Senha do cartão.
     * @return Uma nova instância de CardResponseDTO.
     */
    public static CardResponseDTO fromData(String numeroCartao, String senha) {
        return new CardResponseDTO(senha, numeroCartao);
    }
}