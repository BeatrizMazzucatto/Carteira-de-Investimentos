package com.invest.service;

import com.invest.model.Investidor;
import com.invest.service.AuthService.AuthResult;
import com.invest.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AuthService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AuthService")
class AuthServiceTest {

    @Mock
    private com.invest.service.InvestidorService investidorService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Investidor investidor;

    @BeforeEach
    void setUp() {
        investidor = new Investidor();
        investidor.setId(1L);
        investidor.setNome("João Silva");
        investidor.setEmail("joao@example.com");
        // Senha hasheada com BCrypt para "senha123"
        investidor.setSenha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
    }

    @Test
    @DisplayName("Deve autenticar investidor com credenciais válidas")
    void deveAutenticarInvestidorComCredenciaisValidas() {
        // Arrange
        when(investidorService.getInvestidorByEmail("joao@example.com"))
                .thenReturn(Optional.of(investidor));
        when(jwtUtil.generateToken(any(Long.class), anyString())).thenReturn("token-jwt-valido");

        // Act
        AuthResult result = authService.authenticate("joao@example.com", "senha123");

        // Assert
        assertNotNull(result);
        assertTrue(result.isSucesso());
        assertEquals("token-jwt-valido", result.getToken());
        assertNotNull(result.getInvestidor());
        verify(investidorService, times(1)).getInvestidorByEmail("joao@example.com");
        verify(jwtUtil, times(1)).generateToken(any(Long.class), anyString());
    }

    @Test
    @DisplayName("Deve retornar falha quando email não existe")
    void deveRetornarFalhaQuandoEmailNaoExiste() {
        // Arrange
        when(investidorService.getInvestidorByEmail("inexistente@example.com"))
                .thenReturn(Optional.empty());

        // Act
        AuthResult result = authService.authenticate("inexistente@example.com", "senha123");

        // Assert
        assertNotNull(result);
        assertFalse(result.isSucesso());
        assertTrue(result.getMensagem().toLowerCase().contains("credenciais") ||
                   result.getMensagem().toLowerCase().contains("incorretos"));
        assertNull(result.getToken());
        verify(jwtUtil, never()).generateToken(any(), anyString());
    }

    @Test
    @DisplayName("Deve retornar falha quando senha está incorreta")
    void deveRetornarFalhaQuandoSenhaIncorreta() {
        // Arrange
        when(investidorService.getInvestidorByEmail("joao@example.com"))
                .thenReturn(Optional.of(investidor));

        // Act
        AuthResult result = authService.authenticate("joao@example.com", "senhaErrada");

        // Assert
        assertNotNull(result);
        assertFalse(result.isSucesso());
        assertTrue(result.getMensagem().toLowerCase().contains("credenciais") ||
                   result.getMensagem().toLowerCase().contains("incorretos"));
        assertNull(result.getToken());
        verify(jwtUtil, never()).generateToken(any(), anyString());
    }

    @Test
    @DisplayName("Deve validar token JWT válido")
    void deveValidarTokenJWTValido() {
        // Arrange
        String token = "token-valido";
        when(jwtUtil.validateToken(token)).thenReturn(true);

        // Act
        boolean isValid = authService.validateToken(token);

        // Assert
        assertTrue(isValid);
        verify(jwtUtil, times(1)).validateToken(token);
    }

    @Test
    @DisplayName("Deve retornar false para token inválido")
    void deveRetornarFalseParaTokenInvalido() {
        // Arrange
        String token = "token-invalido";
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // Act
        boolean isValid = authService.validateToken(token);

        // Assert
        assertFalse(isValid);
        verify(jwtUtil, times(1)).validateToken(token);
    }

    @Test
    @DisplayName("Deve obter ID do investidor a partir do token")
    void deveObterIdInvestidorAPartirDoToken() {
        // Arrange
        String token = "token-valido";
        when(jwtUtil.getInvestidorIdFromToken(token)).thenReturn(1L);

        // Act
        Long investidorId = authService.getInvestidorIdFromToken(token);

        // Assert
        assertNotNull(investidorId);
        assertEquals(1L, investidorId);
        verify(jwtUtil, times(1)).getInvestidorIdFromToken(token);
    }

    @Test
    @DisplayName("Deve obter email do investidor a partir do token")
    void deveObterEmailInvestidorAPartirDoToken() {
        // Arrange
        String token = "token-valido";
        when(jwtUtil.getEmailFromToken(token)).thenReturn("joao@example.com");

        // Act
        String email = authService.getEmailFromToken(token);

        // Assert
        assertNotNull(email);
        assertEquals("joao@example.com", email);
        verify(jwtUtil, times(1)).getEmailFromToken(token);
    }
}

