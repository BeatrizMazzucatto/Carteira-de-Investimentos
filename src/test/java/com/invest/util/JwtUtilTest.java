package com.invest.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para JwtUtil
 */
@DisplayName("Testes Unitários - JwtUtil")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret = "testSecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS256AlgorithmSecurity";
    private Long expiration = 86400000L; // 24 horas

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
    }

    @Test
    @DisplayName("Deve gerar token JWT válido")
    void deveGerarTokenValido() {
        // Arrange
        String email = "teste@example.com";
        Long investidorId = 1L;

        // Act
        String token = jwtUtil.generateToken(investidorId, email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes
    }

    @Test
    @DisplayName("Deve extrair email do token")
    void deveExtrairEmailDoToken() {
        // Arrange
        String email = "teste@example.com";
        Long investidorId = 1L;
        String token = jwtUtil.generateToken(investidorId, email);

        // Act
        String emailExtraido = jwtUtil.getEmailFromToken(token);

        // Assert
        assertEquals(email, emailExtraido);
    }

    @Test
    @DisplayName("Deve extrair ID do investidor do token")
    void deveExtrairIdInvestidorDoToken() {
        // Arrange
        String email = "teste@example.com";
        Long investidorId = 1L;
        String token = jwtUtil.generateToken(investidorId, email);

        // Act
        Long idExtraido = jwtUtil.getInvestidorIdFromToken(token);

        // Assert
        assertEquals(investidorId, idExtraido);
    }

    @Test
    @DisplayName("Deve validar token válido")
    void deveValidarTokenValido() {
        // Arrange
        String email = "teste@example.com";
        Long investidorId = 1L;
        String token = jwtUtil.generateToken(investidorId, email);

        // Act
        boolean isValid = jwtUtil.validateToken(token, email);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve invalidar token com email diferente")
    void deveInvalidarTokenEmailDiferente() {
        // Arrange
        String email = "teste@example.com";
        Long investidorId = 1L;
        String token = jwtUtil.generateToken(investidorId, email);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "outro@example.com");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve extrair data de expiração do token")
    void deveExtrairDataExpiracao() {
        // Arrange
        String email = "teste@example.com";
        Long investidorId = 1L;
        String token = jwtUtil.generateToken(investidorId, email);

        // Act
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }
}

