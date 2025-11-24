package com.invest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invest.dto.CarteiraRequest;
import com.invest.model.*;
import com.invest.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para CarteiraController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - CarteiraController")
class CarteiraControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private AtivoRepository ativoRepository;

    private Investidor investidor;
    private Carteira carteira;

    @BeforeEach
    void setUp() {
        // Limpa dados de teste
        ativoRepository.deleteAll();
        carteiraRepository.deleteAll();
        investidorRepository.deleteAll();

        // Cria investidor
        investidor = new Investidor();
        investidor.setNome("Investidor Teste");
        investidor.setEmail("teste@example.com");
        investidor.setSenha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        investidor = investidorRepository.save(investidor);

        // Cria carteira
        carteira = new Carteira();
        carteira.setNome("Carteira Teste");
        carteira.setDescricao("Descrição da carteira");
        carteira.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteira.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteira.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteira.setValorInicial(new BigDecimal("10000.00"));
        carteira.setInvestidor(investidor);
        carteira = carteiraRepository.save(carteira);
    }

    @Test
    @DisplayName("Deve criar uma carteira via API")
    void deveCriarCarteiraViaAPI() throws Exception {
        // Arrange
        CarteiraRequest request = new CarteiraRequest();
        request.setNome("Nova Carteira");
        request.setDescricao("Descrição da nova carteira");
        request.setObjetivo(ObjetivoCarteira.RESERVA_EMERGENCIAL);
        request.setPrazo(PrazoCarteira.CURTO_PRAZO);
        request.setPerfilRisco(PerfilRisco.BAIXO_RISCO);
        request.setValorInicial(new BigDecimal("5000.00"));

        // Act & Assert
        mockMvc.perform(post("/api/carteiras/investidor/{investidorId}", investidor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Nova Carteira"))
                .andExpect(jsonPath("$.objetivo").value("APOSENTADORIA"))
                .andExpect(jsonPath("$.valorInicial").value(5000.00));
    }

    @Test
    @DisplayName("Deve listar carteiras de um investidor")
    void deveListarCarteirasInvestidor() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/carteiras/investidor/{investidorId}", investidor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Carteira Teste"))
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    @DisplayName("Deve buscar carteira por ID")
    void deveBuscarCarteiraPorId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/carteiras/{id}", carteira.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carteira.getId()))
                .andExpect(jsonPath("$.nome").value("Carteira Teste"))
                .andExpect(jsonPath("$.objetivo").exists());
    }

    @Test
    @DisplayName("Deve atualizar uma carteira via API")
    void deveAtualizarCarteiraViaAPI() throws Exception {
        // Arrange
        CarteiraRequest request = new CarteiraRequest();
        request.setNome("Carteira Atualizada");
        request.setDescricao("Nova descrição");
        request.setObjetivo(ObjetivoCarteira.VALORIZACAO_RAPIDA);
        request.setPrazo(PrazoCarteira.MEDIO_PRAZO);
        request.setPerfilRisco(PerfilRisco.ALTO_RISCO);
        request.setValorInicial(new BigDecimal("15000.00"));

        // Act & Assert
        mockMvc.perform(put("/api/carteiras/{id}", carteira.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carteira Atualizada"))
                .andExpect(jsonPath("$.descricao").value("Nova descrição"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando carteira não existe")
    void deveRetornar404QuandoCarteiraNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/carteiras/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar ativos de uma carteira")
    void deveListarAtivosCarteira() throws Exception {
        // Arrange - cria ativo
        Ativo ativo = new Ativo();
        ativo.setCodigo("PETR4");
        ativo.setNome("Petrobras PN");
        ativo.setTipo(TipoAtivo.ACAO);
        ativo.setQuantidade(new BigDecimal("100"));
        ativo.setPrecoCompra(new BigDecimal("25.00"));
        ativo.setPrecoAtual(new BigDecimal("26.00"));
        ativo.setCarteira(carteira);
        ativoRepository.save(ativo);

        // Act & Assert
        mockMvc.perform(get("/api/carteiras/{id}/ativos", carteira.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigo").value("PETR4"));
    }
}

