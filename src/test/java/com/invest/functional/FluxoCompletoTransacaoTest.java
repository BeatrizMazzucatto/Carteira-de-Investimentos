package com.invest.functional;

import com.invest.dto.CarteiraRequest;
import com.invest.dto.TransacaoRequest;
import com.invest.model.*;
import com.invest.repository.*;
import com.invest.service.CarteiraService;
import com.invest.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes funcionais - fluxos completos de negócio
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Funcionais - Fluxos Completos")
class FluxoCompletoTransacaoTest {

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private AtivoRepository ativoRepository;

    @Autowired
    private CarteiraService carteiraService;

    @Autowired
    private TransacaoService transacaoService;

    private Investidor investidor;
    private Carteira carteira;

    @BeforeEach
    void setUp() {
        // Limpa dados
        transacaoRepository.deleteAll();
        ativoRepository.deleteAll();
        carteiraRepository.deleteAll();
        investidorRepository.deleteAll();

        // Cria investidor
        investidor = new Investidor();
        investidor.setNome("Investidor Funcional");
        investidor.setEmail("funcional@example.com");
        investidor.setSenha("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        investidor = investidorRepository.save(investidor);

        // Cria carteira
        CarteiraRequest carteiraRequest = new CarteiraRequest();
        carteiraRequest.setNome("Carteira Funcional");
        carteiraRequest.setObjetivo(ObjetivoCarteira.APOSENTADORIA);
        carteiraRequest.setPrazo(PrazoCarteira.LONGO_PRAZO);
        carteiraRequest.setPerfilRisco(PerfilRisco.MODERADO_RISCO);
        carteiraRequest.setValorInicial(new BigDecimal("10000.00"));

        carteira = carteiraService.createCarteira(investidor.getId(), carteiraRequest);
    }

    @Test
    @DisplayName("Fluxo completo: Criar carteira -> Comprar ativo -> Verificar posição")
    void fluxoCompletoCompraAtivo() {
        // Arrange
        TransacaoRequest compraRequest = new TransacaoRequest();
        compraRequest.setTipoTransacao(TipoTransacao.COMPRA);
        compraRequest.setCodigoAtivo("PETR4");
        compraRequest.setNomeAtivo("Petrobras PN");
        compraRequest.setTipoAtivo(TipoAtivo.ACAO);
        compraRequest.setQuantidade(new BigDecimal("100"));
        compraRequest.setPrecoUnitario(new BigDecimal("25.50"));
        compraRequest.setTaxasCorretagem(new BigDecimal("5.00"));
        compraRequest.setDataTransacao(LocalDateTime.now());

        // Act - Cria transação de compra
        Transacao transacao = transacaoService.createTransacao(carteira.getId(), compraRequest);

        // Assert - Verifica transação
        assertNotNull(transacao);
        assertNotNull(transacao.getId());
        assertEquals(TipoTransacao.COMPRA, transacao.getTipoTransacao());
        assertEquals("PETR4", transacao.getCodigoAtivo());

        // Assert - Verifica ativo criado
        Ativo ativo = ativoRepository.findByCodigoAndCarteira("PETR4", carteira)
                .orElse(null);
        assertNotNull(ativo);
        assertEquals(new BigDecimal("100"), ativo.getQuantidade());
        assertEquals(new BigDecimal("25.50"), ativo.getPrecoCompra());

        // Assert - Verifica carteira atualizada
        Carteira carteiraAtualizada = carteiraRepository.findById(carteira.getId()).orElse(null);
        assertNotNull(carteiraAtualizada);
        assertNotNull(carteiraAtualizada.getValorAtual());
    }

    @Test
    @DisplayName("Fluxo completo: Compra -> Venda -> Verificar posição final")
    void fluxoCompletoCompraVenda() {
        // Arrange - Compra inicial
        TransacaoRequest compraRequest = new TransacaoRequest();
        compraRequest.setTipoTransacao(TipoTransacao.COMPRA);
        compraRequest.setCodigoAtivo("VALE3");
        compraRequest.setNomeAtivo("Vale ON");
        compraRequest.setTipoAtivo(TipoAtivo.ACAO);
        compraRequest.setQuantidade(new BigDecimal("200"));
        compraRequest.setPrecoUnitario(new BigDecimal("50.00"));
        compraRequest.setTaxasCorretagem(new BigDecimal("10.00"));
        compraRequest.setDataTransacao(LocalDateTime.now());

        transacaoService.createTransacao(carteira.getId(), compraRequest);

        // Act - Venda parcial
        TransacaoRequest vendaRequest = new TransacaoRequest();
        vendaRequest.setTipoTransacao(TipoTransacao.VENDA);
        vendaRequest.setCodigoAtivo("VALE3");
        vendaRequest.setNomeAtivo("Vale ON");
        vendaRequest.setTipoAtivo(TipoAtivo.ACAO);
        vendaRequest.setQuantidade(new BigDecimal("100"));
        vendaRequest.setPrecoUnitario(new BigDecimal("55.00"));
        vendaRequest.setTaxasCorretagem(new BigDecimal("5.00"));
        vendaRequest.setImpostos(new BigDecimal("15.00"));
        vendaRequest.setDataTransacao(LocalDateTime.now());

        transacaoService.createTransacao(carteira.getId(), vendaRequest);

        // Assert - Verifica posição final do ativo
        Ativo ativo = ativoRepository.findByCodigoAndCarteira("VALE3", carteira)
                .orElse(null);
        assertNotNull(ativo);
        assertEquals(new BigDecimal("100"), ativo.getQuantidade()); // 200 - 100

        // Assert - Verifica transações
        List<Transacao> transacoes = transacaoRepository.findByCarteiraAndCodigoAtivo(carteira, "VALE3");
        assertEquals(2, transacoes.size());
    }

    @Test
    @DisplayName("Fluxo completo: Múltiplas compras -> Calcular preço médio")
    void fluxoCompletoPrecoMedio() {
        // Arrange - Primeira compra
        TransacaoRequest compra1 = new TransacaoRequest();
        compra1.setTipoTransacao(TipoTransacao.COMPRA);
        compra1.setCodigoAtivo("ITUB4");
        compra1.setNomeAtivo("Itaú PN");
        compra1.setTipoAtivo(TipoAtivo.ACAO);
        compra1.setQuantidade(new BigDecimal("100"));
        compra1.setPrecoUnitario(new BigDecimal("20.00"));
        compra1.setTaxasCorretagem(new BigDecimal("5.00"));
        compra1.setDataTransacao(LocalDateTime.now());
        transacaoService.createTransacao(carteira.getId(), compra1);

        // Act - Segunda compra
        TransacaoRequest compra2 = new TransacaoRequest();
        compra2.setTipoTransacao(TipoTransacao.COMPRA);
        compra2.setCodigoAtivo("ITUB4");
        compra2.setNomeAtivo("Itaú PN");
        compra2.setTipoAtivo(TipoAtivo.ACAO);
        compra2.setQuantidade(new BigDecimal("100"));
        compra2.setPrecoUnitario(new BigDecimal("25.00"));
        compra2.setTaxasCorretagem(new BigDecimal("5.00"));
        compra2.setDataTransacao(LocalDateTime.now());
        transacaoService.createTransacao(carteira.getId(), compra2);

        // Assert - Verifica preço médio
        Ativo ativo = ativoRepository.findByCodigoAndCarteira("ITUB4", carteira)
                .orElse(null);
        assertNotNull(ativo);
        assertEquals(new BigDecimal("200"), ativo.getQuantidade());
        // Preço médio: (100 * 20 + 100 * 25) / 200 = 22.50
        assertEquals(new BigDecimal("22.50"), ativo.getPrecoCompra());
    }

    @Test
    @DisplayName("Fluxo completo: Criar carteira -> Registrar provento -> Verificar impacto")
    void fluxoCompletoProvento() {
        // Arrange - Compra inicial
        TransacaoRequest compraRequest = new TransacaoRequest();
        compraRequest.setTipoTransacao(TipoTransacao.COMPRA);
        compraRequest.setCodigoAtivo("PETR4");
        compraRequest.setNomeAtivo("Petrobras PN");
        compraRequest.setTipoAtivo(TipoAtivo.ACAO);
        compraRequest.setQuantidade(new BigDecimal("100"));
        compraRequest.setPrecoUnitario(new BigDecimal("25.00"));
        compraRequest.setDataTransacao(LocalDateTime.now());
        transacaoService.createTransacao(carteira.getId(), compraRequest);

        // Act - Registra provento
        TransacaoRequest proventoRequest = new TransacaoRequest();
        proventoRequest.setTipoTransacao(TipoTransacao.DIVIDENDO);
        proventoRequest.setCodigoAtivo("PETR4");
        proventoRequest.setNomeAtivo("Petrobras PN");
        proventoRequest.setTipoAtivo(TipoAtivo.ACAO);
        proventoRequest.setQuantidade(new BigDecimal("100"));
        proventoRequest.setPrecoUnitario(new BigDecimal("1.50")); // Dividendo por ação
        proventoRequest.setDataTransacao(LocalDateTime.now());
        Transacao provento = transacaoService.createTransacao(carteira.getId(), proventoRequest);

        // Assert
        assertNotNull(provento);
        assertEquals(TipoTransacao.DIVIDENDO, provento.getTipoTransacao());
        
        // Ativo não deve ter quantidade alterada por provento
        Ativo ativo = ativoRepository.findByCodigoAndCarteira("PETR4", carteira)
                .orElse(null);
        assertNotNull(ativo);
        assertEquals(new BigDecimal("100"), ativo.getQuantidade());
    }
}

