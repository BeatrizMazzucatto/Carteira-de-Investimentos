package com.invest.service;

import com.invest.model.Investidor;
import com.invest.repository.InvestidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para InvestidorService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - InvestidorService")
class InvestidorServiceTest {

    @Mock
    private InvestidorRepository investidorRepository;

    @InjectMocks
    private InvestidorService investidorService;

    private Investidor investidor;

    @BeforeEach
    void setUp() {
        investidor = new Investidor();
        investidor.setId(1L);
        investidor.setNome("João Silva");
        investidor.setEmail("joao@example.com");
        investidor.setSenha("senha123");
    }

    @Test
    @DisplayName("Deve criar um investidor com senha hasheada")
    void deveCriarInvestidorComSenhaHasheada() {
        // Arrange
        when(investidorRepository.save(any(Investidor.class))).thenAnswer(invocation -> {
            Investidor inv = invocation.getArgument(0);
            inv.setId(1L);
            return inv;
        });

        // Act
        Investidor resultado = investidorService.createInvestidor(investidor);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getSenha());
        // Senha deve estar hasheada (começa com $2a$ ou $2b$)
        assertTrue(resultado.getSenha().startsWith("$2a$") || resultado.getSenha().startsWith("$2b$"));
        assertNotEquals("senha123", resultado.getSenha());
        verify(investidorRepository, times(1)).save(any(Investidor.class));
    }

    @Test
    @DisplayName("Deve buscar investidor por ID")
    void deveBuscarInvestidorPorId() {
        // Arrange
        when(investidorRepository.findById(1L)).thenReturn(Optional.of(investidor));

        // Act
        Investidor resultado = investidorService.getInvestidorById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        verify(investidorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando investidor não existe")
    void deveLancarExcecaoInvestidorNaoExiste() {
        // Arrange
        when(investidorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            investidorService.getInvestidorById(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve listar todos os investidores")
    void deveListarTodosInvestidores() {
        // Arrange
        Investidor investidor2 = new Investidor();
        investidor2.setId(2L);
        investidor2.setNome("Maria Santos");

        List<Investidor> investidores = Arrays.asList(investidor, investidor2);
        when(investidorRepository.findAll()).thenReturn(investidores);

        // Act
        List<Investidor> resultado = investidorService.getAllInvestidores();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(investidorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar investidor por email")
    void deveBuscarInvestidorPorEmail() {
        // Arrange
        when(investidorRepository.findByEmailIgnoreCase("joao@example.com")).thenReturn(Optional.of(investidor));

        // Act
        Optional<Investidor> resultado = investidorService.getInvestidorByEmail("joao@example.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("joao@example.com", resultado.get().getEmail());
        verify(investidorRepository, times(1)).findByEmailIgnoreCase("joao@example.com");
    }

    @Test
    @DisplayName("Deve atualizar um investidor")
    void deveAtualizarInvestidor() {
        // Arrange
        Investidor investidorAtualizado = new Investidor();
        investidorAtualizado.setNome("João Silva Atualizado");
        investidorAtualizado.setEmail("joao.novo@example.com");

        when(investidorRepository.findById(1L)).thenReturn(Optional.of(investidor));
        when(investidorRepository.save(any(Investidor.class))).thenReturn(investidor);

        // Act
        Investidor resultado = investidorService.updateInvestidor(1L, investidorAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(investidorRepository, times(1)).save(any(Investidor.class));
    }
}

