package com.invest.integration;

import com.invest.repository.InvestidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para CotacaoRestController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - CotacaoRestController")
class CotacaoRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvestidorRepository investidorRepository;

    @BeforeEach
    void setUp() {
        // Limpa dados de teste se necessário
        investidorRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve listar todas as cotações via API")
    void deveListarTodasCotacoesViaAPI() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/cotacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cotacoes").exists())
                .andExpect(jsonPath("$.total").exists());
    }

    @Test
    @DisplayName("Deve buscar cotação de um ativo específico")
    void deveBuscarCotacaoAtivoEspecifico() throws Exception {
        // Act & Assert
        // Nota: Este teste pode falhar se o ativo não existir no JSON de cotações
        // Em produção, seria necessário mockar ou garantir que o ativo existe
        mockMvc.perform(get("/api/cotacoes/{codigo}", "PETR4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").exists())
                .andExpect(jsonPath("$.preco").exists());
    }

    @Test
    @DisplayName("Deve retornar 404 quando ativo não existe")
    void deveRetornar404QuandoAtivoNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/cotacoes/{codigo}", "INEXISTENTE123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar cotações manualmente via API")
    void deveAtualizarCotacoesManualViaAPI() throws Exception {
        // Act & Assert
        // Nota: Este teste pode demorar se fizer chamada real ao Google Sheets
        mockMvc.perform(post("/api/cotacoes/atualizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());
    }
}

