package com.invest.service;

import com.invest.dto.RelatorioExibicaoResponse;
import com.invest.model.*;
import com.invest.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para RelatorioExibicaoService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RelatorioExibicaoService")
class RelatorioExibicaoServiceTest {

    @Mock
    private InvestidorRepository investidorRepository;

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private AtivoRepository ativoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private RelatorioExibicaoService relatorioExibicaoService;

    private Investidor investidor;
    private Carteira carteira;
    private Ativo ativo;
    private Transacao transacao;

    @BeforeEach
    void setUp() {
        investidor = new Investidor();
        investidor.setId(1L);
        investidor.setNome("João Silva");
        investidor.setEmail("joao@example.com");
        investidor.setDataCriacao(LocalDateTime.now().minusDays(30));
        investidor.setDataAtualizacao(LocalDateTime.now());

        carteira = new Carteira();
        carteira.setId(1L);
        carteira.setNome("Carteira Aposentadoria");
        carteira.setDescricao("Carteira para aposentadoria");
        carteira.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteira.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteira.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteira.setValorInicial(new BigDecimal("10000.00"));
        carteira.setValorAtual(new BigDecimal("10500.00"));
        carteira.setInvestidor(investidor);
        carteira.setDataCriacao(LocalDateTime.now().minusDays(20));
        carteira.setDataAtualizacao(LocalDateTime.now());

        ativo = new Ativo();
        ativo.setId(1L);
        ativo.setCodigo("PETR4");
        ativo.setNome("Petrobras PN");
        ativo.setTipo(TipoAtivo.ACAO);
        ativo.setQuantidade(new BigDecimal("100"));
        ativo.setPrecoCompra(new BigDecimal("25.00"));
        ativo.setPrecoAtual(new BigDecimal("26.00"));
        ativo.setCarteira(carteira);
        ativo.setDataCompra(LocalDateTime.now().minusDays(10));

        transacao = new Transacao();
        transacao.setId(1L);
        transacao.setTipoTransacao(TipoTransacao.COMPRA);
        transacao.setCodigoAtivo("PETR4");
        transacao.setNomeAtivo("Petrobras PN");
        transacao.setTipoAtivo(TipoAtivo.ACAO);
        transacao.setQuantidade(new BigDecimal("100"));
        transacao.setPrecoUnitario(new BigDecimal("25.00"));
        transacao.setValorTotal(new BigDecimal("2500.00"));
        transacao.setDataTransacao(LocalDateTime.now().minusDays(5));
        transacao.setCarteira(carteira);
    }

    @Test
    @DisplayName("Deve gerar relatório de exibição completo")
    void deveGerarRelatorioExibicaoCompleto() {
        // Arrange
        when(investidorRepository.findById(1L)).thenReturn(Optional.of(investidor));
        when(carteiraRepository.findByInvestidor(investidor)).thenReturn(Collections.singletonList(carteira));
        when(ativoRepository.findByCarteira(carteira)).thenReturn(Collections.singletonList(ativo));
        when(transacaoRepository.findByCarteira(carteira)).thenReturn(Collections.singletonList(transacao));
        when(transacaoRepository.countByCarteira(carteira)).thenReturn(1L);

        // Act
        RelatorioExibicaoResponse relatorio = relatorioExibicaoService.gerarRelatorioExibicao(1L);

        // Assert
        assertNotNull(relatorio);
        assertEquals(1L, relatorio.getInvestidorId());
        assertEquals("João Silva", relatorio.getInvestidorNome());
        assertEquals("joao@example.com", relatorio.getInvestidorEmail());
        assertEquals(1, relatorio.getTotalCarteiras());
        assertEquals(1, relatorio.getTotalAtivos());
        assertEquals(1, relatorio.getTotalTransacoes());
        assertNotNull(relatorio.getCarteiras());
        assertEquals(1, relatorio.getCarteiras().size());
        assertNotNull(relatorio.getTransacoesRecentes());
    }

    @Test
    @DisplayName("Deve calcular valores totais corretamente")
    void deveCalcularValoresTotaisCorretamente() {
        // Arrange
        when(investidorRepository.findById(1L)).thenReturn(Optional.of(investidor));
        when(carteiraRepository.findByInvestidor(investidor)).thenReturn(Collections.singletonList(carteira));
        when(ativoRepository.findByCarteira(carteira)).thenReturn(Collections.singletonList(ativo));
        when(transacaoRepository.findByCarteira(carteira)).thenReturn(Collections.singletonList(transacao));
        when(transacaoRepository.countByCarteira(carteira)).thenReturn(1L);

        // Act
        RelatorioExibicaoResponse relatorio = relatorioExibicaoService.gerarRelatorioExibicao(1L);

        // Assert
        assertEquals(new BigDecimal("10000.00"), relatorio.getValorTotalInvestido());
        assertEquals(new BigDecimal("10500.00"), relatorio.getValorTotalAtual());
        assertEquals(new BigDecimal("500.00"), relatorio.getRentabilidadeTotal());
        assertTrue(relatorio.getRentabilidadePercentual().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Deve incluir detalhes das carteiras no relatório")
    void deveIncluirDetalhesCarteirasNoRelatorio() {
        // Arrange
        when(investidorRepository.findById(1L)).thenReturn(Optional.of(investidor));
        when(carteiraRepository.findByInvestidor(investidor)).thenReturn(Collections.singletonList(carteira));
        when(ativoRepository.findByCarteira(carteira)).thenReturn(Collections.singletonList(ativo));
        when(transacaoRepository.findByCarteira(carteira)).thenReturn(Collections.singletonList(transacao));
        when(transacaoRepository.countByCarteira(carteira)).thenReturn(1L);

        // Act
        RelatorioExibicaoResponse relatorio = relatorioExibicaoService.gerarRelatorioExibicao(1L);

        // Assert
        assertNotNull(relatorio.getCarteiras());
        assertEquals(1, relatorio.getCarteiras().size());
        RelatorioExibicaoResponse.CarteiraResumo carteiraResumo = relatorio.getCarteiras().get(0);
        assertEquals(1L, carteiraResumo.getId());
        assertEquals("Carteira Aposentadoria", carteiraResumo.getNome());
        assertEquals(ObjetivoCarteira.APOSENTADORIA, carteiraResumo.getObjetivo());
        assertNotNull(carteiraResumo.getAtivos());
        assertEquals(1, carteiraResumo.getAtivos().size());
    }

    @Test
    @DisplayName("Deve incluir transações recentes ordenadas por data")
    void deveIncluirTransacoesRecentesOrdenadasPorData() {
        // Arrange
        Transacao transacao2 = new Transacao();
        transacao2.setId(2L);
        transacao2.setTipoTransacao(TipoTransacao.VENDA);
        transacao2.setDataTransacao(LocalDateTime.now().minusDays(1));
        transacao2.setCarteira(carteira);

        when(investidorRepository.findById(1L)).thenReturn(Optional.of(investidor));
        when(carteiraRepository.findByInvestidor(investidor)).thenReturn(Collections.singletonList(carteira));
        when(ativoRepository.findByCarteira(carteira)).thenReturn(Collections.singletonList(ativo));
        when(transacaoRepository.findByCarteira(carteira)).thenReturn(List.of(transacao, transacao2));
        when(transacaoRepository.countByCarteira(carteira)).thenReturn(2L);

        // Act
        RelatorioExibicaoResponse relatorio = relatorioExibicaoService.gerarRelatorioExibicao(1L);

        // Assert
        assertNotNull(relatorio.getTransacoesRecentes());
        assertTrue(relatorio.getTransacoesRecentes().size() > 0);
        // Limita a 10 transações mais recentes
        assertTrue(relatorio.getTransacoesRecentes().size() <= 10);
    }

    @Test
    @DisplayName("Deve gerar estatísticas por tipo de ativo")
    void deveGerarEstatisticasPorTipoAtivo() {
        // Arrange
        Ativo ativo2 = new Ativo();
        ativo2.setTipo(TipoAtivo.FII);
        ativo2.setQuantidade(new BigDecimal("50"));
        ativo2.setPrecoCompra(new BigDecimal("100.00"));
        ativo2.setPrecoAtual(new BigDecimal("104.00"));
        ativo2.setCarteira(carteira);

        when(investidorRepository.findById(1L)).thenReturn(Optional.of(investidor));
        when(carteiraRepository.findByInvestidor(investidor)).thenReturn(Collections.singletonList(carteira));
        when(ativoRepository.findByCarteira(carteira)).thenReturn(List.of(ativo, ativo2));
        when(transacaoRepository.findByCarteira(carteira)).thenReturn(Collections.emptyList());
        when(transacaoRepository.countByCarteira(carteira)).thenReturn(0L);

        // Act
        RelatorioExibicaoResponse relatorio = relatorioExibicaoService.gerarRelatorioExibicao(1L);

        // Assert
        assertNotNull(relatorio.getEstatisticasPorTipo());
        assertTrue(relatorio.getEstatisticasPorTipo().containsKey(TipoAtivo.ACAO));
        assertTrue(relatorio.getEstatisticasPorTipo().containsKey(TipoAtivo.FII));
    }

    @Test
    @DisplayName("Deve lançar exceção quando investidor não existe")
    void deveLancarExcecaoQuandoInvestidorNaoExiste() {
        // Arrange
        when(investidorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            relatorioExibicaoService.gerarRelatorioExibicao(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve retornar relatório vazio quando investidor não tem carteiras")
    void deveRetornarRelatorioVazioQuandoInvestidorNaoTemCarteiras() {
        // Arrange
        when(investidorRepository.findById(1L)).thenReturn(Optional.of(investidor));
        when(carteiraRepository.findByInvestidor(investidor)).thenReturn(Collections.emptyList());

        // Act
        RelatorioExibicaoResponse relatorio = relatorioExibicaoService.gerarRelatorioExibicao(1L);

        // Assert
        assertNotNull(relatorio);
        assertEquals(0, relatorio.getTotalCarteiras());
        assertEquals(0, relatorio.getTotalAtivos());
        assertEquals(BigDecimal.ZERO, relatorio.getValorTotalInvestido());
        assertNotNull(relatorio.getCarteiras());
        assertTrue(relatorio.getCarteiras().isEmpty());
    }
}

