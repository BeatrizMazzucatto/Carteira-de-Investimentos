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
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para RentabilidadeController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - RentabilidadeController")
class RentabilidadeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private AtivoRepository ativoRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private Investidor investidor;
    private Carteira carteira;
    private Ativo ativo;

    @BeforeEach
    void setUp() {
        // Limpa dados de teste
        transacaoRepository.deleteAll();
        ativoRepository.deleteAll();
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
        carteira.setValorAtual(new BigDecimal("10500.00"));
        carteira.setInvestidor(investidor);
        carteira = carteiraRepository.save(carteira);

        // Cria ativo
        ativo = new Ativo();
        ativo.setCodigo("PETR4");
        ativo.setNome("Petrobras PN");
        ativo.setTipo(TipoAtivo.ACAO);
        ativo.setQuantidade(new BigDecimal("100"));
        ativo.setPrecoCompra(new BigDecimal("25.00"));
        ativo.setPrecoAtual(new BigDecimal("26.00"));
        ativo.setCarteira(carteira);
        ativo = ativoRepository.save(ativo);

        // Cria transação
        Transacao transacao = new Transacao();
        transacao.setTipoTransacao(TipoTransacao.COMPRA);
        transacao.setCodigoAtivo("PETR4");
        transacao.setNomeAtivo("Petrobras PN");
        transacao.setTipoAtivo(TipoAtivo.ACAO);
        transacao.setQuantidade(new BigDecimal("100"));
        transacao.setPrecoUnitario(new BigDecimal("25.00"));
        transacao.setValorTotal(new BigDecimal("2500.00"));
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setCarteira(carteira);
        transacaoRepository.save(transacao);
    }

    @Test
    @DisplayName("Deve calcular rentabilidade de um ativo via API")
    void deveCalcularRentabilidadeAtivoViaAPI() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/rentabilidade/ativo/{ativoId}", ativo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativoId").value(ativo.getId()))
                .andExpect(jsonPath("$.codigoAtivo").value("PETR4"))
                .andExpect(jsonPath("$.precoMedioCompra").value(25.00))
                .andExpect(jsonPath("$.precoAtual").value(26.00))
                .andExpect(jsonPath("$.rentabilidadeBruta").exists())
                .andExpect(jsonPath("$.rentabilidadePercentualBruta").exists());
    }

    @Test
    @DisplayName("Deve calcular rentabilidade de uma carteira via API")
    void deveCalcularRentabilidadeCarteiraViaAPI() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/rentabilidade/carteira/{carteiraId}", carteira.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carteiraId").value(carteira.getId()))
                .andExpect(jsonPath("$.carteiraNome").value("Carteira Teste"))
                .andExpect(jsonPath("$.valorTotalInvestido").exists())
                .andExpect(jsonPath("$.valorAtualMercado").exists())
                .andExpect(jsonPath("$.rentabilidadeBruta").exists())
                .andExpect(jsonPath("$.rentabilidadePercentualBruta").exists())
                .andExpect(jsonPath("$.ativos").isArray());
    }

    @Test
    @DisplayName("Deve retornar 400 quando ativo não existe")
    void deveRetornar400QuandoAtivoNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/rentabilidade/ativo/{ativoId}", 999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando carteira não existe")
    void deveRetornar400QuandoCarteiraNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/rentabilidade/carteira/{carteiraId}", 999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve calcular resumo de rentabilidade da carteira")
    void deveCalcularResumoRentabilidadeCarteira() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/rentabilidade/carteira/{carteiraId}/resumo", carteira.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carteiraId").value(carteira.getId()))
                .andExpect(jsonPath("$.valorTotalInvestido").exists())
                .andExpect(jsonPath("$.valorAtual").exists())
                .andExpect(jsonPath("$.rentabilidadePercentual").exists());
    }
}

