package com.invest.integration;

import com.invest.model.*;
import com.invest.repository.CarteiraRepository;
import com.invest.repository.InvestidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para CarteiraRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - CarteiraRepository")
class CarteiraRepositoryIntegrationTest {

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private InvestidorRepository investidorRepository;

    private Investidor investidor;

    @BeforeEach
    void setUp() {
        carteiraRepository.deleteAll();
        investidorRepository.deleteAll();

        investidor = new Investidor();
        investidor.setNome("Investidor Teste");
        investidor.setEmail("teste@example.com");
        investidor.setSenha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        investidor = investidorRepository.save(investidor);
    }

    @Test
    @DisplayName("Deve salvar e buscar uma carteira")
    void deveSalvarEBuscarCarteira() {
        // Arrange
        Carteira carteira = new Carteira();
        carteira.setNome("Carteira Teste");
        carteira.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteira.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteira.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteira.setValorInicial(new BigDecimal("10000.00"));
        carteira.setInvestidor(investidor);

        // Act
        Carteira saved = carteiraRepository.save(carteira);
        Carteira found = carteiraRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals("Carteira Teste", found.getNome());
        assertEquals(ObjetivoCarteira.APOSENTADORIA, found.getObjetivo());
        assertEquals(new BigDecimal("10000.00"), found.getValorInicial());
    }

    @Test
    @DisplayName("Deve buscar carteiras por investidor")
    void deveBuscarCarteirasPorInvestidor() {
        // Arrange
        Carteira carteira1 = new Carteira();
        carteira1.setNome("Carteira 1");
        carteira1.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteira1.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteira1.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteira1.setValorInicial(new BigDecimal("10000.00"));
        carteira1.setInvestidor(investidor);
        carteiraRepository.save(carteira1);

        Carteira carteira2 = new Carteira();
        carteira2.setNome("Carteira 2");
        carteira2.setObjetivo(ObjetivoCarteira.RESERVA_EMERGENCIAL);
        carteira2.setPrazo(PrazoCarteira.MEDIO_PRAZO);
        carteira2.setPerfilRisco(PerfilRisco.BAIXO_RISCO);
        carteira2.setValorInicial(new BigDecimal("5000.00"));
        carteira2.setInvestidor(investidor);
        carteiraRepository.save(carteira2);

        // Act
        List<Carteira> carteiras = carteiraRepository.findByInvestidor(investidor);

        // Assert
        assertEquals(2, carteiras.size());
        assertTrue(carteiras.stream().anyMatch(c -> c.getNome().equals("Carteira 1")));
        assertTrue(carteiras.stream().anyMatch(c -> c.getNome().equals("Carteira 2")));
    }

    @Test
    @DisplayName("Deve atualizar uma carteira")
    void deveAtualizarCarteira() {
        // Arrange
        Carteira carteira = new Carteira();
        carteira.setNome("Carteira Original");
        carteira.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteira.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteira.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteira.setValorInicial(new BigDecimal("10000.00"));
        carteira.setInvestidor(investidor);
        carteira = carteiraRepository.save(carteira);

        // Act
        carteira.setNome("Carteira Atualizada");
        carteira.setValorInicial(new BigDecimal("15000.00"));
        Carteira updated = carteiraRepository.save(carteira);

        // Assert
        assertEquals("Carteira Atualizada", updated.getNome());
        assertEquals(new BigDecimal("15000.00"), updated.getValorInicial());
    }

    @Test
    @DisplayName("Deve deletar uma carteira")
    void deveDeletarCarteira() {
        // Arrange
        Carteira carteira = new Carteira();
        carteira.setNome("Carteira para Deletar");
        carteira.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteira.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteira.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteira.setValorInicial(new BigDecimal("10000.00"));
        carteira.setInvestidor(investidor);
        carteira = carteiraRepository.save(carteira);
        Long id = carteira.getId();

        // Act
        carteiraRepository.deleteById(id);

        // Assert
        assertFalse(carteiraRepository.findById(id).isPresent());
    }
}

