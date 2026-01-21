package com.testerv.miniautorizador.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    public Card(String cardNumber, String password) {
        this.cardNumber = cardNumber;
        this.password = password;
        this.balance = new BigDecimal("500.00");
    }
}
