package com.invest.service;

import com.invest.dto.TransacaoRequest;
import com.invest.model.*;
import com.invest.repository.AtivoRepository;
import com.invest.repository.CarteiraRepository;
import com.invest.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TransacaoService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - TransacaoService")
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private AtivoRepository ativoRepository;

    @Mock
    private CarteiraService carteiraService;

    @InjectMocks
    private TransacaoService transacaoService;

    private Carteira carteira;
    private TransacaoRequest transacaoRequest;

    @BeforeEach
    void setUp() {
        // Setup carteira
        carteira = new Carteira();
        carteira.setId(1L);
        carteira.setNome("Carteira Teste");
        carteira.setValorInicial(new BigDecimal("10000.00"));

        Investidor investidor = new Investidor();
        investidor.setId(1L);
        investidor.setNome("Investidor Teste");
        carteira.setInvestidor(investidor);

        // Setup transação request
        transacaoRequest = new TransacaoRequest();
        transacaoRequest.setTipoTransacao(TipoTransacao.COMPRA);
        transacaoRequest.setCodigoAtivo("PETR4");
        transacaoRequest.setNomeAtivo("Petrobras PN");
        transacaoRequest.setTipoAtivo(TipoAtivo.ACAO);
        transacaoRequest.setQuantidade(new BigDecimal("100"));
        transacaoRequest.setPrecoUnitario(new BigDecimal("25.50"));
        transacaoRequest.setTaxasCorretagem(new BigDecimal("5.00"));
        transacaoRequest.setImpostos(BigDecimal.ZERO);
        transacaoRequest.setDataTransacao(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar uma transação de compra com sucesso")
    void deveCriarTransacaoCompraComSucesso() {
        // Arrange
        when(carteiraRepository.findById(1L)).thenReturn(Optional.of(carteira));
        when(ativoRepository.findByCodigoAndCarteira("PETR4", carteira))
                .thenReturn(Optional.empty());
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(ativoRepository.save(any(Ativo.class))).thenAnswer(invocation -> {
            Ativo a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });

        // Act
        Transacao resultado = transacaoService.createTransacao(1L, transacaoRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(TipoTransacao.COMPRA, resultado.getTipoTransacao());
        assertEquals("PETR4", resultado.getCodigoAtivo());
        assertEquals(new BigDecimal("100"), resultado.getQuantidade());
        verify(transacaoRepository, times(1)).save(any(Transacao.class));
        verify(carteiraService, times(1)).calcularValorAtualCarteira(carteira);
    }

    @Test
    @DisplayName("Deve lançar exceção quando carteira não existe")
    void deveLancarExcecaoCarteiraNaoExiste() {
        // Arrange
        when(carteiraRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transacaoService.createTransacao(999L, transacaoRequest);
        });

        assertEquals("Carteira não encontrada: 999", exception.getMessage());
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar uma transação existente")
    void deveAtualizarTransacaoExistente() {
        // Arrange
        Transacao transacaoExistente = new Transacao();
        transacaoExistente.setId(1L);
        transacaoExistente.setTipoTransacao(TipoTransacao.COMPRA);
        transacaoExistente.setQuantidade(new BigDecimal("50"));
        transacaoExistente.setCarteira(carteira);

        Ativo ativo = new Ativo();
        ativo.setId(1L);
        ativo.setQuantidade(new BigDecimal("50"));
        transacaoExistente.setAtivo(ativo);

        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacaoExistente));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacaoExistente);
        when(ativoRepository.save(any(Ativo.class))).thenReturn(ativo);

        // Atualiza request
        transacaoRequest.setQuantidade(new BigDecimal("100"));

        // Act
        Transacao resultado = transacaoService.updateTransacao(1L, transacaoRequest);

        // Assert
        assertNotNull(resultado);
        verify(transacaoRepository, times(1)).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve deletar uma transação")
    void deveDeletarTransacao() {
        // Arrange
        Transacao transacao = new Transacao();
        transacao.setId(1L);
        transacao.setCarteira(carteira);
        transacao.setTipoTransacao(TipoTransacao.COMPRA);
        transacao.setQuantidade(new BigDecimal("100"));

        Ativo ativo = new Ativo();
        ativo.setId(1L);
        transacao.setAtivo(ativo);

        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));
        doNothing().when(transacaoRepository).deleteById(1L);
        when(ativoRepository.save(any(Ativo.class))).thenReturn(ativo);

        // Act
        transacaoService.deleteTransacao(1L);

        // Assert
        verify(transacaoRepository, times(1)).deleteById(1L);
        verify(carteiraService, times(1)).calcularValorAtualCarteira(carteira);
    }

    @Test
    @DisplayName("Deve calcular estatísticas da carteira")
    void deveCalcularEstatisticasCarteira() {
        // Arrange
        when(carteiraRepository.findById(1L)).thenReturn(Optional.of(carteira));
        when(transacaoRepository.calcularValorTotalCompras(carteira))
                .thenReturn(new BigDecimal("5000.00"));
        when(transacaoRepository.calcularValorTotalVendas(carteira))
                .thenReturn(new BigDecimal("2000.00"));
        when(transacaoRepository.calcularValorTotalProventos(carteira))
                .thenReturn(new BigDecimal("500.00"));

        // Act
        TransacaoService.CarteiraStats stats = transacaoService.calcularEstatisticasCarteira(1L);

        // Assert
        assertNotNull(stats);
        assertEquals(new BigDecimal("5000.00"), stats.getValorTotalCompras());
        assertEquals(new BigDecimal("2000.00"), stats.getValorTotalVendas());
        assertEquals(new BigDecimal("500.00"), stats.getValorTotalProventos());
        assertEquals(new BigDecimal("-2500.00"), stats.getValorLiquido());
    }
}

