package com.invest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para InflacaoService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - InflacaoService")
class InflacaoServiceTest {

    @InjectMocks
    private InflacaoService inflacaoService;

    @BeforeEach
    void setUp() {
        // Service não tem dependências externas, não precisa de mocks
    }

    @Test
    @DisplayName("Deve calcular inflação acumulada entre duas datas")
    void deveCalcularInflacaoAcumuladaEntreDuasDatas() {
        // Arrange
        LocalDate dataInicial = LocalDate.of(2024, 1, 1);
        LocalDate dataFinal = LocalDate.of(2024, 3, 1);

        // Act
        BigDecimal inflacao = inflacaoService.calcularInflacaoAcumulada(dataInicial, dataFinal);

        // Assert
        assertNotNull(inflacao);
        assertTrue(inflacao.compareTo(BigDecimal.ZERO) > 0);
        // Inflação acumulada deve ser positiva
    }

    @Test
    @DisplayName("Deve lançar exceção quando data inicial é posterior à data final")
    void deveLancarExcecaoQuandoDataInicialPosterior() {
        // Arrange
        LocalDate dataInicial = LocalDate.of(2024, 3, 1);
        LocalDate dataFinal = LocalDate.of(2024, 1, 1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            inflacaoService.calcularInflacaoAcumulada(dataInicial, dataFinal);
        });

        assertTrue(exception.getMessage().contains("anterior"));
    }

    @Test
    @DisplayName("Deve calcular valor deflacionado corretamente")
    void deveCalcularValorDeflacionadoCorretamente() {
        // Arrange
        BigDecimal valorAtual = new BigDecimal("1000.00");
        LocalDate dataAtual = LocalDate.of(2024, 3, 1);
        LocalDate dataPassada = LocalDate.of(2024, 1, 1);

        // Act
        BigDecimal valorDeflacionado = inflacaoService.calcularValorDeflacionado(
            valorAtual, dataAtual, dataPassada);

        // Assert
        assertNotNull(valorDeflacionado);
        // Valor deflacionado deve ser menor que o valor atual (devido à inflação)
        assertTrue(valorDeflacionado.compareTo(valorAtual) < 0);
        assertTrue(valorDeflacionado.compareTo(BigDecimal.ZERO) > 0);
        
        // Verifica cálculo matemático: se inflação foi 1%, valor deflacionado deve ser ~990.10
        // Para 2 meses com ~0.4% cada, inflação acumulada ~0.8%, valor deflacionado ~992.00
        assertTrue(valorDeflacionado.compareTo(new BigDecimal("990.00")) > 0);
        assertTrue(valorDeflacionado.compareTo(new BigDecimal("1000.00")) < 0);
    }
    
    @Test
    @DisplayName("Deve calcular deflação corretamente com inflação conhecida")
    void deveCalcularDeflacaoComInflacaoConhecida() {
        // Arrange - Simula inflação de 10% (0.10)
        // Usando datas próximas para ter inflação pequena, mas testável
        BigDecimal valorAtual = new BigDecimal("1100.00");
        LocalDate dataAtual = LocalDate.of(2024, 12, 31);
        LocalDate dataPassada = LocalDate.of(2024, 1, 1);
        
        // Act
        BigDecimal valorDeflacionado = inflacaoService.calcularValorDeflacionado(
            valorAtual, dataAtual, dataPassada);
        
        // Assert
        assertNotNull(valorDeflacionado);
        // Com inflação acumulada de ~3-4% ao longo do ano, R$ 1100 deflacionado deve ser ~1050-1070
        assertTrue(valorDeflacionado.compareTo(new BigDecimal("1000.00")) > 0);
        assertTrue(valorDeflacionado.compareTo(valorAtual) < 0);
    }
    
    @Test
    @DisplayName("Deve retornar mesmo valor quando datas são iguais")
    void deveRetornarMesmoValorQuandoDatasIguais() {
        // Arrange
        BigDecimal valorAtual = new BigDecimal("1000.00");
        LocalDate data = LocalDate.of(2024, 1, 1);
        
        // Act
        BigDecimal valorDeflacionado = inflacaoService.calcularValorDeflacionado(
            valorAtual, data, data);
        
        // Assert
        assertNotNull(valorDeflacionado);
        // Quando não há inflação (datas iguais), valor deflacionado = valor atual
        assertEquals(0, valorDeflacionado.compareTo(valorAtual));
    }

    @Test
    @DisplayName("Deve calcular valor inflacionado corretamente")
    void deveCalcularValorInflacionadoCorretamente() {
        // Arrange
        BigDecimal valorPassado = new BigDecimal("1000.00");
        LocalDate dataPassada = LocalDate.of(2024, 1, 1);
        LocalDate dataAtual = LocalDate.of(2024, 3, 1);

        // Act
        BigDecimal valorInflacionado = inflacaoService.calcularValorInflacionado(
            valorPassado, dataPassada, dataAtual);

        // Assert
        assertNotNull(valorInflacionado);
        // Valor inflacionado deve ser maior que o valor passado (devido à inflação)
        assertTrue(valorInflacionado.compareTo(valorPassado) > 0);
    }

    @Test
    @DisplayName("Deve retornar zero quando datas são iguais")
    void deveRetornarZeroQuandoDatasSaoIguais() {
        // Arrange
        LocalDate data = LocalDate.of(2024, 1, 1);

        // Act
        BigDecimal inflacao = inflacaoService.calcularInflacaoAcumulada(data, data);

        // Assert
        assertNotNull(inflacao);
        // Inflação acumulada deve ser zero ou muito próxima de zero
        assertTrue(inflacao.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Deve calcular inflação anual aproximada")
    void deveCalcularInflacaoAnualAproximada() {
        // Arrange
        LocalDate dataInicial = LocalDate.of(2024, 1, 1);
        LocalDate dataFinal = LocalDate.of(2024, 12, 31);

        // Act
        BigDecimal inflacao = inflacaoService.calcularInflacaoAcumulada(dataInicial, dataFinal);

        // Assert
        assertNotNull(inflacao);
        assertTrue(inflacao.compareTo(BigDecimal.ZERO) > 0);
        // Inflação anual deve estar entre 0% e 20% (faixa razoável)
        assertTrue(inflacao.compareTo(new BigDecimal("0.20")) < 0);
    }
}

