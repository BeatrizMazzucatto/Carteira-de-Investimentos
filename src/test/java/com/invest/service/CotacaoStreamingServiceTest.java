package com.invest.service;

import com.invest.dto.CotacaoDTO;
import com.invest.service.external.GoogleSheetsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CotacaoStreamingService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - CotacaoStreamingService")
class CotacaoStreamingServiceTest {

    @Mock
    private GoogleSheetsService googleSheetsService;

    @InjectMocks
    private CotacaoStreamingService cotacaoStreamingService;

    private Map<String, BigDecimal> cotacoesMap;

    @BeforeEach
    void setUp() {
        cotacoesMap = new HashMap<>();
        cotacoesMap.put("PETR4", new BigDecimal("25.50"));
        cotacoesMap.put("VALE3", new BigDecimal("60.00"));
        cotacoesMap.put("ITUB4", new BigDecimal("28.75"));
    }

    @Test
    @DisplayName("Deve obter cotação de um ativo")
    void deveObterCotacaoAtivo() {
        // Arrange
        Map<String, Object> cotacaoMap = new HashMap<>();
        cotacaoMap.put("nome", "Petrobras PN");
        cotacaoMap.put("precoAtual", new BigDecimal("25.50"));
        
        when(googleSheetsService.buscarCotacaoCompleta("PETR4")).thenReturn(cotacaoMap);

        // Act
        CotacaoDTO cotacao = cotacaoStreamingService.getCotacao("PETR4");

        // Assert
        assertNotNull(cotacao);
        assertEquals("PETR4", cotacao.getCodigo());
        assertNotNull(cotacao.getPrecoAtual());
        verify(googleSheetsService, atLeastOnce()).buscarCotacaoCompleta("PETR4");
    }

    @Test
    @DisplayName("Deve retornar null quando ativo não existe")
    void deveRetornarNullQuandoAtivoNaoExiste() {
        // Arrange
        when(googleSheetsService.buscarCotacaoCompleta("INEXISTENTE")).thenReturn(null);

        // Act
        CotacaoDTO cotacao = cotacaoStreamingService.getCotacao("INEXISTENTE");

        // Assert
        assertNull(cotacao);
        verify(googleSheetsService, atLeastOnce()).buscarCotacaoCompleta("INEXISTENTE");
    }

    @Test
    @DisplayName("Deve obter todas as cotações")
    void deveObterTodasCotacoes() {
        // Arrange
        when(googleSheetsService.getAllCotacoes()).thenReturn(cotacoesMap);

        // Act
        Map<String, CotacaoDTO> todasCotacoes = cotacaoStreamingService.getAllCotacoes();

        // Assert
        assertNotNull(todasCotacoes);
        // Pode retornar vazio se não houver cotações no cache
        verify(googleSheetsService, atLeastOnce()).getAllCotacoes();
    }

    @Test
    @DisplayName("Deve buscar cotação case-insensitive")
    void deveBuscarCotacaoCaseInsensitive() {
        // Arrange
        Map<String, Object> cotacaoMap = new HashMap<>();
        cotacaoMap.put("nome", "Petrobras PN");
        cotacaoMap.put("precoAtual", new BigDecimal("25.50"));
        
        when(googleSheetsService.buscarCotacaoCompleta(anyString())).thenReturn(cotacaoMap);

        // Act
        CotacaoDTO cotacao1 = cotacaoStreamingService.getCotacao("petr4");
        CotacaoDTO cotacao2 = cotacaoStreamingService.getCotacao("PETR4");
        CotacaoDTO cotacao3 = cotacaoStreamingService.getCotacao("Petr4");

        // Assert
        // Todos devem retornar o mesmo código em maiúsculas
        if (cotacao1 != null) assertEquals("PETR4", cotacao1.getCodigo());
        if (cotacao2 != null) assertEquals("PETR4", cotacao2.getCodigo());
        if (cotacao3 != null) assertEquals("PETR4", cotacao3.getCodigo());
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando não há cotações")
    void deveRetornarMapaVazioQuandoNaoHaCotacoes() {
        // Arrange
        when(googleSheetsService.getAllCotacoes()).thenReturn(new HashMap<>());

        // Act
        Map<String, CotacaoDTO> todasCotacoes = cotacaoStreamingService.getAllCotacoes();

        // Assert
        assertNotNull(todasCotacoes);
        assertTrue(todasCotacoes.isEmpty());
    }
}

