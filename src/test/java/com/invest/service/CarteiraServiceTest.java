package com.invest.service;

import com.invest.dto.CarteiraRequest;
import com.invest.model.*;
import com.invest.repository.AtivoRepository;
import com.invest.repository.CarteiraRepository;
import com.invest.service.external.GoogleSheetsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CarteiraService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - CarteiraService")
class CarteiraServiceTest {

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private GoogleSheetsService googleSheetsService;

    @Mock
    private AtivoRepository ativoRepository;

    @InjectMocks
    private CarteiraService carteiraService;

    private Investidor investidor;
    private CarteiraRequest carteiraRequest;

    @BeforeEach
    void setUp() {
        investidor = new Investidor();
        investidor.setId(1L);
        investidor.setNome("Investidor Teste");
        investidor.setEmail("teste@example.com");

        carteiraRequest = new CarteiraRequest();
        carteiraRequest.setNome("Carteira de Testes");
        carteiraRequest.setDescricao("Descrição da carteira");
        carteiraRequest.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteiraRequest.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteiraRequest.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteiraRequest.setValorInicial(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Deve criar uma carteira com sucesso")
    void deveCriarCarteiraComSucesso() {
        // Arrange
        when(carteiraRepository.save(any(Carteira.class))).thenAnswer(invocation -> {
            Carteira c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        // Act
        Carteira resultado = carteiraService.createCarteira(investidor, carteiraRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals("Carteira de Testes", resultado.getNome());
        assertEquals(ObjetivoCarteira.APOSENTADORIA, resultado.getObjetivo());
        assertEquals(new BigDecimal("10000.00"), resultado.getValorInicial());
        verify(carteiraRepository, times(1)).save(any(Carteira.class));
    }

    @Test
    @DisplayName("Deve atualizar uma carteira existente")
    void deveAtualizarCarteiraExistente() {
        // Arrange
        Carteira carteira = new Carteira();
        carteira.setId(1L);
        carteira.setNome("Carteira Antiga");
        carteira.setInvestidor(investidor);

        carteiraRequest.setNome("Carteira Atualizada");
        carteiraRequest.setDescricao("Nova descrição");

        when(carteiraRepository.save(any(Carteira.class))).thenReturn(carteira);

        // Act
        Carteira resultado = carteiraService.updateCarteira(carteira, carteiraRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals("Carteira Atualizada", resultado.getNome());
        assertEquals("Nova descrição", resultado.getDescricao());
        verify(carteiraRepository, times(1)).save(carteira);
    }

    @Test
    @DisplayName("Deve fazer update parcial de uma carteira")
    void deveFazerUpdateParcialCarteira() {
        // Arrange
        Carteira carteira = new Carteira();
        carteira.setId(1L);
        carteira.setNome("Carteira Original");
        carteira.setDescricao("Descrição original");
        carteira.setInvestidor(investidor);

        CarteiraRequest requestParcial = new CarteiraRequest();
        requestParcial.setNome("Novo Nome");
        // Descrição não é fornecida

        when(carteiraRepository.save(any(Carteira.class))).thenReturn(carteira);

        // Act
        Carteira resultado = carteiraService.patchCarteira(carteira, requestParcial);

        // Assert
        assertNotNull(resultado);
        assertEquals("Novo Nome", resultado.getNome());
        // Descrição original deve ser mantida
        verify(carteiraRepository, times(1)).save(carteira);
    }

    @Test
    @DisplayName("Deve buscar carteira por ID")
    void deveBuscarCarteiraPorId() {
        // Arrange
        Carteira carteira = new Carteira();
        carteira.setId(1L);
        carteira.setNome("Carteira Teste");
        carteira.setInvestidor(investidor);

        when(carteiraRepository.findById(1L)).thenReturn(Optional.of(carteira));

        // Act
        Carteira resultado = carteiraService.getCarteiraById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Carteira Teste", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando carteira não existe")
    void deveLancarExcecaoCarteiraNaoExiste() {
        // Arrange
        when(carteiraRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carteiraService.getCarteiraById(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve listar todas as carteiras de um investidor")
    void deveListarCarteirasInvestidor() {
        // Arrange
        Carteira carteira1 = new Carteira();
        carteira1.setId(1L);
        carteira1.setNome("Carteira 1");
        carteira1.setInvestidor(investidor);

        Carteira carteira2 = new Carteira();
        carteira2.setId(2L);
        carteira2.setNome("Carteira 2");
        carteira2.setInvestidor(investidor);

        List<Carteira> carteiras = Arrays.asList(carteira1, carteira2);

        Investidor investidor = new Investidor();
        investidor.setId(1L);
        when(carteiraRepository.findByInvestidor(investidor)).thenReturn(carteiras);

        // Act
        List<Carteira> resultado = carteiraService.getCarteirasByInvestidor(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(carteiraRepository, times(1)).findByInvestidor(any(Investidor.class));
    }
}

