package com.testerv.miniautorizador.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testerv.miniautorizador.dto.CardRequestDTO;
import com.testerv.miniautorizador.dto.TransactionDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String DEFAULT_PASSWORD = "1234";

    private String generateCardNumber() {
        return "7" + System.nanoTime();
    }

    @Test
    @DisplayName("Should return 201 and OK body when transaction is authorized")
    @WithMockUser(username = "username", roles = "USER")
    void shouldAuthorizeTransactionWithSuccess() throws Exception {
        String cardNumber = generateCardNumber();
        CardRequestDTO cardRequest = new CardRequestDTO(cardNumber, DEFAULT_PASSWORD);

        mockMvc.perform(post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardRequest)));

        TransactionDTO transaction = new TransactionDTO(cardNumber, DEFAULT_PASSWORD, new BigDecimal("10.00"));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));
    }

    @Test
    @DisplayName("Should return 422 and SENHA_INVALIDA when password does not match")
    @WithMockUser(username = "username", roles = "USER")
    void shouldReturnUnprocessableEntityWhenPasswordIsWrong() throws Exception {
        String cardNumber = generateCardNumber();
        CardRequestDTO cardRequest = new CardRequestDTO(cardNumber, DEFAULT_PASSWORD);

        mockMvc.perform(post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardRequest)));

        TransactionDTO wrongPassTransaction = new TransactionDTO(cardNumber, "9999", new BigDecimal("10.00"));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPassTransaction)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SENHA_INVALIDA"));
    }

    @Test
    @DisplayName("Should return 422 and SALDO_INSUFICIENTE when balance is low")
    @WithMockUser(username = "username", roles = "USER")
    void shouldReturnUnprocessableEntityWhenBalanceIsInsufficient() throws Exception {
        String cardNumber = generateCardNumber();
        CardRequestDTO cardRequest = new CardRequestDTO(cardNumber, DEFAULT_PASSWORD);

        mockMvc.perform(post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardRequest)));

        TransactionDTO expensiveTransaction = new TransactionDTO(cardNumber, DEFAULT_PASSWORD, new BigDecimal("600.00"));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expensiveTransaction)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SALDO_INSUFICIENTE"));
    }

    @Test
    @DisplayName("Should return 422 and CARTAO_INEXISTENTE when card number is not found")
    @WithMockUser(username = "username", roles = "USER")
    void shouldReturnUnprocessableEntityWhenCardDoesNotExist() throws Exception {
        String cardNumber = "000" + System.nanoTime();
        TransactionDTO transaction = new TransactionDTO(cardNumber, DEFAULT_PASSWORD, new BigDecimal("10.00"));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("CARTAO_INEXISTENTE"));
    }
}
