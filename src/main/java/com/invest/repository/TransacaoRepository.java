package com.invest.repository;

import com.invest.model.Carteira;
import com.invest.model.TipoTransacao;
import com.invest.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para operações de banco de dados da entidade Transacao
 */
@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
    /**
     * Busca todas as transações de uma carteira
     */
    List<Transacao> findByCarteira(Carteira carteira);
    
    /**
     * Busca transações de uma carteira com paginação
     */
    Page<Transacao> findByCarteira(Carteira carteira, Pageable pageable);
    
    /**
     * Busca transações por carteira e tipo
     */
    List<Transacao> findByCarteiraAndTipoTransacao(Carteira carteira, TipoTransacao tipoTransacao);
    
    /**
     * Busca transações por carteira e código do ativo
     */
    List<Transacao> findByCarteiraAndCodigoAtivo(Carteira carteira, String codigoAtivo);
    
    /**
     * Busca transações por carteira e período
     */
    List<Transacao> findByCarteiraAndDataTransacaoBetween(
            Carteira carteira, LocalDateTime dataInicio, LocalDateTime dataFim);
    
    /**
     * Busca transações por carteira, tipo e período
     */
    List<Transacao> findByCarteiraAndTipoTransacaoAndDataTransacaoBetween(
            Carteira carteira, TipoTransacao tipoTransacao, 
            LocalDateTime dataInicio, LocalDateTime dataFim);
    
    /**
     * Busca transações por carteira e código do ativo com paginação
     */
    Page<Transacao> findByCarteiraAndCodigoAtivo(Carteira carteira, String codigoAtivo, Pageable pageable);
    
    /**
     * Busca transações por carteira e tipo com paginação
     */
    Page<Transacao> findByCarteiraAndTipoTransacao(Carteira carteira, TipoTransacao tipoTransacao, Pageable pageable);
    
    /**
     * Conta o número de transações de uma carteira
     */
    long countByCarteira(Carteira carteira);
    
    /**
     * Conta transações por tipo em uma carteira
     */
    long countByCarteiraAndTipoTransacao(Carteira carteira, TipoTransacao tipoTransacao);
    
    /**
     * Calcula o valor total de compras de uma carteira
     */
    @Query("SELECT COALESCE(SUM(t.valorTotal), 0) FROM Transacao t WHERE t.carteira = :carteira AND t.tipoTransacao = 'COMPRA'")
    java.math.BigDecimal calcularValorTotalCompras(@Param("carteira") Carteira carteira);
    
    /**
     * Calcula o valor total de vendas de uma carteira
     */
    @Query("SELECT COALESCE(SUM(t.valorTotal), 0) FROM Transacao t WHERE t.carteira = :carteira AND t.tipoTransacao = 'VENDA'")
    java.math.BigDecimal calcularValorTotalVendas(@Param("carteira") Carteira carteira);
    
    /**
     * Calcula o valor total de proventos de uma carteira
     */
    @Query("SELECT COALESCE(SUM(t.valorTotal), 0) FROM Transacao t WHERE t.carteira = :carteira AND t.tipoTransacao IN ('PROVENTO', 'DIVIDENDO', 'JCP', 'RENDIMENTO')")
    java.math.BigDecimal calcularValorTotalProventos(@Param("carteira") Carteira carteira);
    
    /**
     * Busca transações de compra de um ativo específico
     */
    @Query("SELECT t FROM Transacao t WHERE t.carteira = :carteira AND t.codigoAtivo = :codigoAtivo AND t.tipoTransacao = 'COMPRA' ORDER BY t.dataTransacao")
    List<Transacao> findComprasByCarteiraAndCodigoAtivo(@Param("carteira") Carteira carteira, @Param("codigoAtivo") String codigoAtivo);
    
    /**
     * Busca transações de venda de um ativo específico
     */
    @Query("SELECT t FROM Transacao t WHERE t.carteira = :carteira AND t.codigoAtivo = :codigoAtivo AND t.tipoTransacao = 'VENDA' ORDER BY t.dataTransacao")
    List<Transacao> findVendasByCarteiraAndCodigoAtivo(@Param("carteira") Carteira carteira, @Param("codigoAtivo") String codigoAtivo);
    
    /**
     * Busca transações de proventos de um ativo específico
     */
    @Query("SELECT t FROM Transacao t WHERE t.carteira = :carteira AND t.codigoAtivo = :codigoAtivo AND t.tipoTransacao IN ('PROVENTO', 'DIVIDENDO', 'JCP', 'RENDIMENTO') ORDER BY t.dataTransacao")
    List<Transacao> findProventosByCarteiraAndCodigoAtivo(@Param("carteira") Carteira carteira, @Param("codigoAtivo") String codigoAtivo);
    
    /**
     * Busca a última transação de um ativo
     */
    @Query("SELECT t FROM Transacao t WHERE t.carteira = :carteira AND t.codigoAtivo = :codigoAtivo ORDER BY t.dataTransacao DESC")
    List<Transacao> findUltimaTransacaoByCarteiraAndCodigoAtivo(@Param("carteira") Carteira carteira, @Param("codigoAtivo") String codigoAtivo);
    
    /**
     * Busca uma transação por ID com JOIN FETCH para evitar lazy loading
     */
    @Query("SELECT t FROM Transacao t LEFT JOIN FETCH t.carteira LEFT JOIN FETCH t.ativo WHERE t.id = :id")
    java.util.Optional<Transacao> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Calcula a quantidade total comprada de um ativo
     */
    @Query("SELECT COALESCE(SUM(t.quantidade), 0) FROM Transacao t WHERE t.carteira = :carteira AND t.codigoAtivo = :codigoAtivo AND t.tipoTransacao = 'COMPRA'")
    java.math.BigDecimal calcularQuantidadeTotalComprada(@Param("carteira") Carteira carteira, @Param("codigoAtivo") String codigoAtivo);
    
    /**
     * Calcula a quantidade total vendida de um ativo
     */
    @Query("SELECT COALESCE(SUM(t.quantidade), 0) FROM Transacao t WHERE t.carteira = :carteira AND t.codigoAtivo = :codigoAtivo AND t.tipoTransacao = 'VENDA'")
    java.math.BigDecimal calcularQuantidadeTotalVendida(@Param("carteira") Carteira carteira, @Param("codigoAtivo") String codigoAtivo);
}
