package com.invest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invest.dto.TransacaoRequest;
import com.invest.model.*;
import com.invest.repository.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para TransacaoController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - TransacaoController")
class TransacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private AtivoRepository ativoRepository;

    private Investidor investidor;
    private Carteira carteira;

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
        carteira.setInvestidor(investidor);
        carteira = carteiraRepository.save(carteira);
    }

    @Test
    @DisplayName("Deve criar uma transação via API")
    void deveCriarTransacaoViaAPI() throws Exception {
        // Arrange
        TransacaoRequest request = new TransacaoRequest();
        request.setTipoTransacao(TipoTransacao.COMPRA);
        request.setCodigoAtivo("PETR4");
        request.setNomeAtivo("Petrobras PN");
        request.setTipoAtivo(TipoAtivo.ACAO);
        request.setQuantidade(new BigDecimal("100"));
        request.setPrecoUnitario(new BigDecimal("25.50"));
        request.setTaxasCorretagem(new BigDecimal("5.00"));

        // Act & Assert
        mockMvc.perform(post("/api/transacoes/carteira/{carteiraId}", carteira.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoTransacao").value("COMPRA"))
                .andExpect(jsonPath("$.codigoAtivo").value("PETR4"))
                .andExpect(jsonPath("$.quantidade").value(100));
    }

    @Test
    @DisplayName("Deve listar transações de uma carteira")
    void deveListarTransacoesCarteira() throws Exception {
        // Arrange - cria transação
        Transacao transacao = new Transacao();
        transacao.setTipoTransacao(TipoTransacao.COMPRA);
        transacao.setCodigoAtivo("PETR4");
        transacao.setNomeAtivo("Petrobras PN");
        transacao.setTipoAtivo(TipoAtivo.ACAO);
        transacao.setQuantidade(new BigDecimal("100"));
        transacao.setPrecoUnitario(new BigDecimal("25.50"));
        transacao.setCarteira(carteira);
        transacao.setDataTransacao(LocalDateTime.now());
        transacaoRepository.save(transacao);

        // Act & Assert
        mockMvc.perform(get("/api/transacoes/carteira/{carteiraId}", carteira.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigoAtivo").value("PETR4"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando carteira não existe")
    void deveRetornar404CarteiraNaoExiste() throws Exception {
        // Arrange
        TransacaoRequest request = new TransacaoRequest();
        request.setTipoTransacao(TipoTransacao.COMPRA);
        request.setCodigoAtivo("PETR4");
        request.setNomeAtivo("Petrobras PN");
        request.setTipoAtivo(TipoAtivo.ACAO);
        request.setQuantidade(new BigDecimal("100"));
        request.setPrecoUnitario(new BigDecimal("25.50"));

        // Act & Assert
        mockMvc.perform(post("/api/transacoes/carteira/{carteiraId}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios")
    void deveValidarCamposObrigatorios() throws Exception {
        // Arrange - request sem campos obrigatórios
        TransacaoRequest request = new TransacaoRequest();
        // Campos obrigatórios não preenchidos

        // Act & Assert
        mockMvc.perform(post("/api/transacoes/carteira/{carteiraId}", carteira.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

