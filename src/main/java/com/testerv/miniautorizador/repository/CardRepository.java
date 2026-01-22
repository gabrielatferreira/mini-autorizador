package com.testerv.miniautorizador.repository;

import com.testerv.miniautorizador.model.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface de repositório para operações de persistência da entidade {@link Card}.
 * <p>
 * Estende {@link JpaRepository} para fornecer operações padrão de CRUD e utiliza
 * recursos do Spring Data JPA para consultas customizadas com controle de concorrência.
 * </p>
 */
@Repository
public interface CardRepository extends JpaRepository<Card, String> {

    /**
     * Busca um cartão pelo seu número, aplicando um bloqueio pessimista de escrita.
     * <p>
     * O uso de {@link LockModeType#PESSIMISTIC_WRITE} garante que, durante uma transação,
     * nenhum outro processo consiga ler ou alterar este registro até que a transação atual
     * seja finalizada (commit ou rollback). Isso evita o problema de "Lost Update" em
     * cenários de alta concorrência de transações financeiras.
     * </p>
     *
     * @param cardNumber O número do cartão a ser localizado.
     * @return Um {@link Optional} contendo o cartão encontrado, ou vazio caso não exista.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber")
    Optional<Card> findByCardNumberWithLock(@Param("cardNumber") String cardNumber);
}
