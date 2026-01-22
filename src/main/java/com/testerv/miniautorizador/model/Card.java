package com.testerv.miniautorizador.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidade que representa um cartão de benefícios no sistema.
 * <p>
 * Esta classe é mapeada para a tabela "cards" no banco de dados PostgreSQL.
 * O saldo inicial padrão de cada novo cartão é definido como 500.00.
 * </p>
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @Column(name = "card_number", length = 16)
    private String cardNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @Version
    private Long version;

    /**
     * Construtor customizado para criação de novos cartões.
     * Define automaticamente o saldo inicial de 500.00 conforme os requisitos do desafio.
     * * @param cardNumber O número do cartão a ser criado.
     * @param password A senha do novo cartão.
     */
    public Card(String cardNumber, String password) {
        this.cardNumber = cardNumber;
        this.password = password;
        this.balance = new BigDecimal("500.00");
    }
}
