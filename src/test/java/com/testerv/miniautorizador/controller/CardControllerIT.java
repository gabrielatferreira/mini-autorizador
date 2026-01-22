package com.testerv.miniautorizador.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testerv.miniautorizador.dto.CardRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CardControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String DEFAULT_PASSWORD = "1234";

    private String generateCardNumber() {
        return "8" + System.nanoTime();
    }

    @Test
    @DisplayName("Should create a new card with 201 status and initial balance")
    @WithMockUser(username = "username", roles = "USER")
    void shouldCreateNewCardWithSuccess() throws Exception {
        String cardNumber = generateCardNumber();
        CardRequestDTO request = new CardRequestDTO(cardNumber, DEFAULT_PASSWORD);

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCartao").value(request.numeroCartao()))
                .andExpect(jsonPath("$.saldo").value(500.00));
    }

    @Test
    @DisplayName("Should return 422 and JSON without balance when card already exists")
    @WithMockUser(username = "username", roles = "USER")
    void shouldReturn422WithoutBalanceWhenCardExists() throws Exception {
        String cardNumber = generateCardNumber();
        CardRequestDTO request = new CardRequestDTO(cardNumber, DEFAULT_PASSWORD);

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.numeroCartao").value(request.numeroCartao()))
                .andExpect(jsonPath("$", not(hasKey("saldo"))));
    }

    @Test
    @DisplayName("Should return card balance with success")
    @WithMockUser(username = "username", roles = "USER")
    void shouldReturnBalanceWithSuccess() throws Exception {
        String cardNumber = generateCardNumber();
        CardRequestDTO request = new CardRequestDTO(cardNumber, DEFAULT_PASSWORD);

        mockMvc.perform(post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/cartoes/" + cardNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("500.00"));
    }

    @Test
    @DisplayName("Should return 404 when getting balance of non-existent card")
    @WithMockUser(username = "username", roles = "USER")
    void shouldReturn404WhenCardNotFound() throws Exception {
        mockMvc.perform(get("/cartoes/999999999"))
                .andExpect(status().isNotFound());
    }
}