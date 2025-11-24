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
 * Testes de integração para RelatorioExibicaoController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - RelatorioExibicaoController")
class RelatorioExibicaoControllerIntegrationTest {

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
        carteira.setDescricao("Descrição da carteira");
        carteira.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteira.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteira.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteira.setValorInicial(new BigDecimal("10000.00"));
        carteira.setValorAtual(new BigDecimal("10500.00"));
        carteira.setInvestidor(investidor);
        carteira = carteiraRepository.save(carteira);

        // Cria ativo
        Ativo ativo = new Ativo();
        ativo.setCodigo("PETR4");
        ativo.setNome("Petrobras PN");
        ativo.setTipo(TipoAtivo.ACAO);
        ativo.setQuantidade(new BigDecimal("100"));
        ativo.setPrecoCompra(new BigDecimal("25.00"));
        ativo.setPrecoAtual(new BigDecimal("26.00"));
        ativo.setCarteira(carteira);
        ativoRepository.save(ativo);

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
    @DisplayName("Deve retornar relatório de exibição do investidor via API")
    void deveRetornarRelatorioExibicaoInvestidorViaAPI() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/relatorio/investidor/{investidorId}", investidor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.investidorId").value(investidor.getId()))
                .andExpect(jsonPath("$.investidorNome").value("Investidor Teste"))
                .andExpect(jsonPath("$.totalCarteiras").value(1))
                .andExpect(jsonPath("$.totalAtivos").value(1))
                .andExpect(jsonPath("$.totalTransacoes").value(1))
                .andExpect(jsonPath("$.valorTotalInvestido").value(10000.00))
                .andExpect(jsonPath("$.valorTotalAtual").value(10500.00))
                .andExpect(jsonPath("$.carteiras").isArray())
                .andExpect(jsonPath("$.carteiras[0].nome").value("Carteira Teste"))
                .andExpect(jsonPath("$.transacoesRecentes").isArray())
                .andExpect(jsonPath("$.estatisticasPorTipo").exists());
    }

    @Test
    @DisplayName("Deve retornar relatório consolidado da empresa via API")
    void deveRetornarRelatorioConsolidadoEmpresaViaAPI() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/relatorio/empresa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInvestidores").value(1))
                .andExpect(jsonPath("$.totalCarteiras").value(1))
                .andExpect(jsonPath("$.totalAtivos").value(1))
                .andExpect(jsonPath("$.totalTransacoes").value(1))
                .andExpect(jsonPath("$.valorTotalInvestido").value(10000.00))
                .andExpect(jsonPath("$.valorTotalAtual").value(10500.00))
                .andExpect(jsonPath("$.investidores").isArray())
                .andExpect(jsonPath("$.investidores[0].nome").value("Investidor Teste"))
                .andExpect(jsonPath("$.estatisticasPorTipo").exists())
                .andExpect(jsonPath("$.transacoesRecentes").isArray())
                .andExpect(jsonPath("$.dataGeracao").exists())
                .andExpect(jsonPath("$.versao").value("1.0"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando investidor não existe")
    void deveRetornar400QuandoInvestidorNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/relatorio/investidor/{investidorId}", 999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar relatório da empresa mesmo sem investidores")
    void deveRetornarRelatorioEmpresaSemInvestidores() throws Exception {
        // Arrange - limpa todos os dados
        transacaoRepository.deleteAll();
        ativoRepository.deleteAll();
        carteiraRepository.deleteAll();
        investidorRepository.deleteAll();

        // Act & Assert
        mockMvc.perform(get("/api/relatorio/empresa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInvestidores").value(0))
                .andExpect(jsonPath("$.totalCarteiras").value(0))
                .andExpect(jsonPath("$.totalAtivos").value(0))
                .andExpect(jsonPath("$.investidores").isArray())
                .andExpect(jsonPath("$.investidores").isEmpty());
    }

    @Test
    @DisplayName("Deve incluir múltiplos investidores no relatório da empresa")
    void deveIncluirMultiplosInvestidoresNoRelatorioEmpresa() throws Exception {
        // Arrange - cria segundo investidor
        Investidor investidor2 = new Investidor();
        investidor2.setNome("Investidor 2");
        investidor2.setEmail("investidor2@example.com");
        investidor2.setSenha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        investidor2 = investidorRepository.save(investidor2);

        Carteira carteira2 = new Carteira();
        carteira2.setNome("Carteira 2");
        carteira2.setObjetivo(ObjetivoCarteira.RESERVA_EMERGENCIAL);
        carteira2.setPrazo(PrazoCarteira.CURTO_PRAZO);
        carteira2.setPerfilRisco(PerfilRisco.BAIXO_RISCO);
        carteira2.setValorInicial(new BigDecimal("5000.00"));
        carteira2.setValorAtual(new BigDecimal("5100.00"));
        carteira2.setInvestidor(investidor2);
        carteiraRepository.save(carteira2);

        // Act & Assert
        mockMvc.perform(get("/api/relatorio/empresa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInvestidores").value(2))
                .andExpect(jsonPath("$.totalCarteiras").value(2))
                .andExpect(jsonPath("$.investidores").isArray())
                .andExpect(jsonPath("$.investidores.length()").value(2))
                .andExpect(jsonPath("$.valorTotalInvestido").value(15000.00))
                .andExpect(jsonPath("$.valorTotalAtual").value(15600.00));
    }
}

