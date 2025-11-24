package com.invest.service;

import com.invest.dto.RelatorioEmpresaResponse;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para RelatorioEmpresaService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RelatorioEmpresaService")
class RelatorioEmpresaServiceTest {

    @Mock
    private InvestidorRepository investidorRepository;

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private AtivoRepository ativoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private RelatorioEmpresaService relatorioEmpresaService;

    private Investidor investidor1;
    private Investidor investidor2;
    private Carteira carteira1;
    private Carteira carteira2;
    private Ativo ativo1;
    private Ativo ativo2;
    private Transacao transacao1;

    @BeforeEach
    void setUp() {
        // Investidor 1
        investidor1 = new Investidor();
        investidor1.setId(1L);
        investidor1.setNome("João Silva");
        investidor1.setEmail("joao@example.com");
        investidor1.setDataCriacao(LocalDateTime.now().minusDays(30));

        // Investidor 2
        investidor2 = new Investidor();
        investidor2.setId(2L);
        investidor2.setNome("Maria Santos");
        investidor2.setEmail("maria@example.com");
        investidor2.setDataCriacao(LocalDateTime.now().minusDays(20));

        // Carteira 1
        carteira1 = new Carteira();
        carteira1.setId(1L);
        carteira1.setNome("Carteira Aposentadoria");
        carteira1.setInvestidor(investidor1);
        carteira1.setValorInicial(new BigDecimal("10000.00"));
        carteira1.setValorAtual(new BigDecimal("10500.00"));

        // Carteira 2
        carteira2 = new Carteira();
        carteira2.setId(2L);
        carteira2.setNome("Carteira Reserva");
        carteira2.setInvestidor(investidor2);
        carteira2.setValorInicial(new BigDecimal("5000.00"));
        carteira2.setValorAtual(new BigDecimal("5200.00"));

        // Ativo 1
        ativo1 = new Ativo();
        ativo1.setId(1L);
        ativo1.setCodigo("PETR4");
        ativo1.setNome("Petrobras PN");
        ativo1.setTipo(TipoAtivo.ACAO);
        ativo1.setQuantidade(new BigDecimal("100"));
        ativo1.setPrecoCompra(new BigDecimal("25.00"));
        ativo1.setPrecoAtual(new BigDecimal("26.00"));
        ativo1.setCarteira(carteira1);

        // Ativo 2
        ativo2 = new Ativo();
        ativo2.setId(2L);
        ativo2.setCodigo("HGLG11");
        ativo2.setNome("CSHG Logística");
        ativo2.setTipo(TipoAtivo.FII);
        ativo2.setQuantidade(new BigDecimal("50"));
        ativo2.setPrecoCompra(new BigDecimal("100.00"));
        ativo2.setPrecoAtual(new BigDecimal("104.00"));
        ativo2.setCarteira(carteira2);

        // Transação 1
        transacao1 = new Transacao();
        transacao1.setId(1L);
        transacao1.setTipoTransacao(TipoTransacao.COMPRA);
        transacao1.setCodigoAtivo("PETR4");
        transacao1.setNomeAtivo("Petrobras PN");
        transacao1.setTipoAtivo(TipoAtivo.ACAO);
        transacao1.setQuantidade(new BigDecimal("100"));
        transacao1.setPrecoUnitario(new BigDecimal("25.00"));
        transacao1.setValorTotal(new BigDecimal("2500.00"));
        transacao1.setDataTransacao(LocalDateTime.now().minusDays(5));
        transacao1.setCarteira(carteira1);
    }

    @Test
    @DisplayName("Deve gerar relatório consolidado com múltiplos investidores")
    void deveGerarRelatorioConsolidadoComMultiplosInvestidores() {
        // Arrange
        List<Investidor> investidores = Arrays.asList(investidor1, investidor2);
        when(investidorRepository.findAll()).thenReturn(investidores);
        when(carteiraRepository.findByInvestidor(investidor1)).thenReturn(Collections.singletonList(carteira1));
        when(carteiraRepository.findByInvestidor(investidor2)).thenReturn(Collections.singletonList(carteira2));
        when(ativoRepository.findByCarteira(carteira1)).thenReturn(Collections.singletonList(ativo1));
        when(ativoRepository.findByCarteira(carteira2)).thenReturn(Collections.singletonList(ativo2));
        when(transacaoRepository.findByCarteira(carteira1)).thenReturn(Collections.singletonList(transacao1));
        when(transacaoRepository.findByCarteira(carteira2)).thenReturn(Collections.emptyList());
        when(transacaoRepository.countByCarteira(carteira1)).thenReturn(1L);
        when(transacaoRepository.countByCarteira(carteira2)).thenReturn(0L);

        // Act
        RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();

        // Assert
        assertNotNull(relatorio);
        assertEquals(2, relatorio.getTotalInvestidores());
        assertEquals(2, relatorio.getTotalCarteiras());
        assertEquals(2, relatorio.getTotalAtivos());
        assertEquals(1, relatorio.getTotalTransacoes());
        assertNotNull(relatorio.getDataGeracao());
        assertEquals("1.0", relatorio.getVersao());
        assertEquals(2, relatorio.getInvestidores().size());
    }

    @Test
    @DisplayName("Deve calcular valores totais corretamente")
    void deveCalcularValoresTotaisCorretamente() {
        // Arrange
        List<Investidor> investidores = Arrays.asList(investidor1, investidor2);
        when(investidorRepository.findAll()).thenReturn(investidores);
        when(carteiraRepository.findByInvestidor(investidor1)).thenReturn(Collections.singletonList(carteira1));
        when(carteiraRepository.findByInvestidor(investidor2)).thenReturn(Collections.singletonList(carteira2));
        when(ativoRepository.findByCarteira(carteira1)).thenReturn(Collections.singletonList(ativo1));
        when(ativoRepository.findByCarteira(carteira2)).thenReturn(Collections.singletonList(ativo2));
        when(transacaoRepository.findByCarteira(any())).thenReturn(Collections.emptyList());
        when(transacaoRepository.countByCarteira(any())).thenReturn(0L);

        // Act
        RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();

        // Assert
        assertEquals(new BigDecimal("15000.00"), relatorio.getValorTotalInvestido());
        assertEquals(new BigDecimal("15700.00"), relatorio.getValorTotalAtual());
        assertEquals(new BigDecimal("700.00"), relatorio.getRentabilidadeTotal());
        assertTrue(relatorio.getRentabilidadePercentual().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Deve gerar estatísticas por tipo de ativo")
    void deveGerarEstatisticasPorTipoAtivo() {
        // Arrange
        List<Investidor> investidores = Collections.singletonList(investidor1);
        when(investidorRepository.findAll()).thenReturn(investidores);
        when(carteiraRepository.findByInvestidor(investidor1)).thenReturn(Collections.singletonList(carteira1));
        when(ativoRepository.findByCarteira(carteira1)).thenReturn(Collections.singletonList(ativo1));
        when(transacaoRepository.findByCarteira(any())).thenReturn(Collections.emptyList());
        when(transacaoRepository.countByCarteira(any())).thenReturn(0L);

        // Act
        RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();

        // Assert
        assertNotNull(relatorio.getEstatisticasPorTipo());
        assertTrue(relatorio.getEstatisticasPorTipo().containsKey("ACAO"));
        RelatorioEmpresaResponse.EstatisticaTipoAtivo estat = relatorio.getEstatisticasPorTipo().get("ACAO");
        assertNotNull(estat);
        assertEquals("ACAO", estat.getTipo());
        assertEquals(1, estat.getQuantidade());
    }

    @Test
    @DisplayName("Deve incluir transações recentes ordenadas por data")
    void deveIncluirTransacoesRecentesOrdenadasPorData() {
        // Arrange
        Transacao transacao2 = new Transacao();
        transacao2.setId(2L);
        transacao2.setTipoTransacao(TipoTransacao.VENDA);
        transacao2.setCodigoAtivo("VALE3");
        transacao2.setDataTransacao(LocalDateTime.now().minusDays(1));
        transacao2.setCarteira(carteira2);

        List<Investidor> investidores = Arrays.asList(investidor1, investidor2);
        when(investidorRepository.findAll()).thenReturn(investidores);
        when(carteiraRepository.findByInvestidor(investidor1)).thenReturn(Collections.singletonList(carteira1));
        when(carteiraRepository.findByInvestidor(investidor2)).thenReturn(Collections.singletonList(carteira2));
        when(ativoRepository.findByCarteira(any())).thenReturn(Collections.emptyList());
        when(transacaoRepository.findByCarteira(carteira1)).thenReturn(Collections.singletonList(transacao1));
        when(transacaoRepository.findByCarteira(carteira2)).thenReturn(Collections.singletonList(transacao2));
        when(transacaoRepository.countByCarteira(any())).thenReturn(1L);

        // Act
        RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();

        // Assert
        assertNotNull(relatorio.getTransacoesRecentes());
        assertTrue(relatorio.getTransacoesRecentes().size() > 0);
        // Transação mais recente deve vir primeiro
        assertEquals("VALE3", relatorio.getTransacoesRecentes().get(0).getCodigoAtivo());
    }

    @Test
    @DisplayName("Deve retornar relatório vazio quando não há investidores")
    void deveRetornarRelatorioVazioQuandoNaoHaInvestidores() {
        // Arrange
        when(investidorRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();

        // Assert
        assertNotNull(relatorio);
        assertEquals(0, relatorio.getTotalInvestidores());
        assertEquals(0, relatorio.getTotalCarteiras());
        assertEquals(0, relatorio.getTotalAtivos());
        assertEquals(0, relatorio.getTotalTransacoes());
        assertEquals(BigDecimal.ZERO, relatorio.getValorTotalInvestido());
        assertEquals(BigDecimal.ZERO, relatorio.getValorTotalAtual());
        assertNotNull(relatorio.getInvestidores());
        assertTrue(relatorio.getInvestidores().isEmpty());
    }

    @Test
    @DisplayName("Deve calcular rentabilidade percentual corretamente")
    void deveCalcularRentabilidadePercentualCorretamente() {
        // Arrange
        List<Investidor> investidores = Collections.singletonList(investidor1);
        when(investidorRepository.findAll()).thenReturn(investidores);
        when(carteiraRepository.findByInvestidor(investidor1)).thenReturn(Collections.singletonList(carteira1));
        when(ativoRepository.findByCarteira(any())).thenReturn(Collections.emptyList());
        when(transacaoRepository.findByCarteira(any())).thenReturn(Collections.emptyList());
        when(transacaoRepository.countByCarteira(any())).thenReturn(0L);

        // Act
        RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();

        // Assert
        // Valor investido: 10000, Valor atual: 10500, Rentabilidade: 500 (5%)
        assertEquals(new BigDecimal("10000.00"), relatorio.getValorTotalInvestido());
        assertEquals(new BigDecimal("10500.00"), relatorio.getValorTotalAtual());
        assertEquals(new BigDecimal("500.00"), relatorio.getRentabilidadeTotal());
        // Rentabilidade percentual deve ser aproximadamente 5%
        assertTrue(relatorio.getRentabilidadePercentual().compareTo(new BigDecimal("4.99")) > 0);
        assertTrue(relatorio.getRentabilidadePercentual().compareTo(new BigDecimal("5.01")) < 0);
    }
}

