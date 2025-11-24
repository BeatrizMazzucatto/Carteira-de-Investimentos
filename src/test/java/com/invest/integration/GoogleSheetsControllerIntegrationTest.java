package com.invest.integration;

import com.invest.model.*;
import com.invest.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para GoogleSheetsController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - GoogleSheetsController")
class GoogleSheetsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    private Investidor investidor;
    private Carteira carteira;

    @BeforeEach
    void setUp() {
        // Limpa dados de teste
        carteiraRepository.deleteAll();
        investidorRepository.deleteAll();

        // Cria investidor
        investidor = new Investidor();
        investidor.setNome("Investidor Teste");
        investidor.setEmail("teste@example.com");
        investidor.setSenha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        investidor = investidorRepository.save(investidor);

        // Cria carteira
        carteira = new Carteira();
        carteira.setNome("Carteira Teste");
        carteira.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteira.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteira.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteira.setValorInicial(new BigDecimal("10000.00"));
        carteira.setInvestidor(investidor);
        carteira = carteiraRepository.save(carteira);
    }

    @Test
    @DisplayName("Deve buscar preço de um ativo via API")
    void deveBuscarPrecoAtivoViaAPI() throws Exception {
        // Act & Assert
        // Nota: Este teste pode falhar se o ativo não existir no JSON de cotações
        // Em produção, seria necessário mockar ou garantir que o ativo existe
        mockMvc.perform(get("/api/google-sheets/preco/{codigoAtivo}", "PETR4"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 quando ativo não existe")
    void deveRetornar404QuandoAtivoNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/google-sheets/preco/{codigoAtivo}", "INEXISTENTE123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar todos os ativos")
    void deveListarTodosAtivos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/google-sheets/ativos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar informações completas de um ativo")
    void deveBuscarInformacoesCompletasAtivo() throws Exception {
        // Act & Assert
        // Nota: Este teste pode falhar se o ativo não existir no JSON
        mockMvc.perform(get("/api/google-sheets/ativo/{codigoAtivo}", "PETR4"))
                .andExpect(status().isOk());
    }
}

