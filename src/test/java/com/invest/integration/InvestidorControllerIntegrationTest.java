package com.invest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invest.model.Investidor;
import com.invest.repository.InvestidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para InvestidorControllerAdaptado
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - InvestidorController")
class InvestidorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvestidorRepository investidorRepository;

    private Investidor investidor;

    @BeforeEach
    void setUp() {
        // Limpa dados de teste
        investidorRepository.deleteAll();

        // Cria investidor
        investidor = new Investidor();
        investidor.setNome("Investidor Teste");
        investidor.setEmail("teste@example.com");
        investidor.setSenha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        investidor = investidorRepository.save(investidor);
    }

    @Test
    @DisplayName("Deve criar um investidor via API")
    void deveCriarInvestidorViaAPI() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("nome", "Novo Investidor");
        request.put("email", "novo@example.com");
        request.put("senha", "senha123");

        // Act & Assert
        mockMvc.perform(post("/api/investidores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Investidor"))
                .andExpect(jsonPath("$.email").value("novo@example.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Deve listar todos os investidores")
    void deveListarTodosInvestidores() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/investidores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Investidor Teste"))
                .andExpect(jsonPath("$[0].email").value("teste@example.com"));
    }

    @Test
    @DisplayName("Deve buscar investidor por ID")
    void deveBuscarInvestidorPorId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/investidores/{id}", investidor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(investidor.getId()))
                .andExpect(jsonPath("$.nome").value("Investidor Teste"))
                .andExpect(jsonPath("$.email").value("teste@example.com"));
    }

    @Test
    @DisplayName("Deve atualizar um investidor via API")
    void deveAtualizarInvestidorViaAPI() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("nome", "Investidor Atualizado");
        request.put("email", "atualizado@example.com");

        // Act & Assert
        mockMvc.perform(put("/api/investidores/{id}", investidor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Investidor Atualizado"))
                .andExpect(jsonPath("$.email").value("atualizado@example.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando investidor não existe")
    void deveRetornar404QuandoInvestidorNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/investidores/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios ao criar investidor")
    void deveValidarCamposObrigatorios() throws Exception {
        // Arrange - request sem campos obrigatórios
        Map<String, String> request = new HashMap<>();
        // Campos obrigatórios não preenchidos

        // Act & Assert
        mockMvc.perform(post("/api/investidores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

