package com.invest.service;

import com.invest.dto.CarteiraRentabilidadeResponse;
import com.invest.dto.RentabilidadeResponse;
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
 * Testes unitários para RentabilidadeService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RentabilidadeService")
class RentabilidadeServiceTest {

    @Mock
    private AtivoRepository ativoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CarteiraRepository carteiraRepository;

    @InjectMocks
    private RentabilidadeService rentabilidadeService;

    private Carteira carteira;
    private Ativo ativo;
    private Transacao transacao;

    @BeforeEach
    void setUp() {
        carteira = new Carteira();
        carteira.setId(1L);
        carteira.setNome("Carteira Teste");
        carteira.setValorInicial(new BigDecimal("10000.00"));
        carteira.setValorAtual(new BigDecimal("10500.00"));
        carteira.setDataCriacao(LocalDateTime.now().minusDays(30));
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
        transacao.setDataTransacao(LocalDateTime.now().minusDays(10));
        transacao.setCarteira(carteira);
    }

    @Test
    @DisplayName("Deve calcular rentabilidade de um ativo")
    void deveCalcularRentabilidadeAtivo() {
        // Arrange
        when(ativoRepository.findById(1L)).thenReturn(Optional.of(ativo));
        when(transacaoRepository.findByCarteiraAndCodigoAtivo(carteira, "PETR4"))
                .thenReturn(Collections.singletonList(transacao));

        // Act
        RentabilidadeResponse response = rentabilidadeService.calcularRentabilidadeAtivo(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getAtivoId());
        assertEquals("PETR4", response.getCodigoAtivo());
        assertEquals("Petrobras PN", response.getNomeAtivo());
        assertEquals(new BigDecimal("100"), response.getQuantidadeAtual());
        assertEquals(new BigDecimal("25.00"), response.getPrecoMedioCompra());
        assertEquals(new BigDecimal("26.00"), response.getPrecoAtual());
        assertNotNull(response.getValorTotalInvestido());
        assertNotNull(response.getValorAtualMercado());
        assertNotNull(response.getRentabilidadeBruta());
    }

    @Test
    @DisplayName("Deve calcular rentabilidade de uma carteira")
    void deveCalcularRentabilidadeCarteira() {
        // Arrange
        when(carteiraRepository.findById(1L)).thenReturn(Optional.of(carteira));
        when(ativoRepository.findByCarteira(carteira)).thenReturn(Collections.singletonList(ativo));
        when(transacaoRepository.findByCarteiraAndCodigoAtivo(carteira, "PETR4"))
                .thenReturn(Collections.singletonList(transacao));

        // Act
        CarteiraRentabilidadeResponse response = rentabilidadeService.calcularRentabilidadeCarteira(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getCarteiraId());
        assertEquals("Carteira Teste", response.getCarteiraNome());
        assertNotNull(response.getValorTotalInvestido());
        assertNotNull(response.getValorAtualMercado());
        assertNotNull(response.getRentabilidadeBruta());
        assertNotNull(response.getRentabilidadePercentualBruta());
        assertNotNull(response.getAtivos());
        assertEquals(1, response.getAtivos().size());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ativo não existe")
    void deveLancarExcecaoQuandoAtivoNaoExiste() {
        // Arrange
        when(ativoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rentabilidadeService.calcularRentabilidadeAtivo(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando carteira não existe")
    void deveLancarExcecaoQuandoCarteiraNaoExiste() {
        // Arrange
        when(carteiraRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rentabilidadeService.calcularRentabilidadeCarteira(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve calcular rentabilidade percentual corretamente")
    void deveCalcularRentabilidadePercentualCorretamente() {
        // Arrange
        when(ativoRepository.findById(1L)).thenReturn(Optional.of(ativo));
        when(transacaoRepository.findByCarteiraAndCodigoAtivo(carteira, "PETR4"))
                .thenReturn(Collections.singletonList(transacao));

        // Act
        RentabilidadeResponse response = rentabilidadeService.calcularRentabilidadeAtivo(1L);

        // Assert
        assertNotNull(response.getRentabilidadePercentualBruta());
        // Preço compra: 25.00, Preço atual: 26.00, Rentabilidade: 4%
        assertTrue(response.getRentabilidadePercentualBruta().compareTo(new BigDecimal("3.99")) > 0);
        assertTrue(response.getRentabilidadePercentualBruta().compareTo(new BigDecimal("4.01")) < 0);
    }

    @Test
    @DisplayName("Deve calcular rentabilidade com múltiplos ativos na carteira")
    void deveCalcularRentabilidadeComMultiplosAtivos() {
        // Arrange
        Ativo ativo2 = new Ativo();
        ativo2.setId(2L);
        ativo2.setCodigo("VALE3");
        ativo2.setTipo(TipoAtivo.ACAO);
        ativo2.setQuantidade(new BigDecimal("50"));
        ativo2.setPrecoCompra(new BigDecimal("60.00"));
        ativo2.setPrecoAtual(new BigDecimal("63.00"));
        ativo2.setCarteira(carteira);

        when(carteiraRepository.findById(1L)).thenReturn(Optional.of(carteira));
        when(ativoRepository.findByCarteira(carteira)).thenReturn(List.of(ativo, ativo2));
        when(transacaoRepository.findByCarteiraAndCodigoAtivo(carteira, "PETR4"))
                .thenReturn(Collections.singletonList(transacao));
        when(transacaoRepository.findByCarteiraAndCodigoAtivo(carteira, "VALE3"))
                .thenReturn(Collections.emptyList());

        // Act
        CarteiraRentabilidadeResponse response = rentabilidadeService.calcularRentabilidadeCarteira(1L);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getAtivos().size());
        assertTrue(response.getValorTotalInvestido().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(response.getValorAtualMercado().compareTo(response.getValorTotalInvestido()) > 0);
    }
}

