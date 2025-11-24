package com.invest.repository;

import com.invest.model.Ativo;
import com.invest.model.Carteira;
import com.invest.model.TipoAtivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Ativo
 */
@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Long> {
    
    /**
     * Busca todos os ativos de uma carteira
     */
    List<Ativo> findByCarteira(Carteira carteira);
    
    /**
     * Busca ativos de uma carteira com paginação
     */
    Page<Ativo> findByCarteira(Carteira carteira, Pageable pageable);
    
    /**
     * Busca ativo por código e carteira
     */
    Optional<Ativo> findByCodigoAndCarteira(String codigo, Carteira carteira);
    
    /**
     * Busca ativos por nome (case insensitive) de uma carteira
     */
    Page<Ativo> findByCarteiraAndNomeContainingIgnoreCase(Carteira carteira, String nome, Pageable pageable);
    
    /**
     * Busca ativos por código (case insensitive) de uma carteira
     */
    Page<Ativo> findByCarteiraAndCodigoContainingIgnoreCase(Carteira carteira, String codigo, Pageable pageable);
    
    /**
     * Conta o número de ativos de uma carteira
     */
    long countByCarteira(Carteira carteira);
    
    /**
     * Busca ativos que precisam de atualização de preços
     */
    @Query("SELECT a FROM Ativo a WHERE a.dataAtualizacao < :dataLimite OR a.dataAtualizacao IS NULL")
    List<Ativo> findAtivosParaAtualizacao(@Param("dataLimite") java.time.LocalDateTime dataLimite);
    
    /**
     * Busca ativos por tipo em uma carteira
     */
    @Query("SELECT a FROM Ativo a WHERE a.carteira = :carteira AND a.tipo = :tipo")
    List<Ativo> findByCarteiraAndTipo(@Param("carteira") Carteira carteira, @Param("tipo") TipoAtivo tipo);
    
    /**
     * Calcula o valor total de uma carteira
     */
    @Query("SELECT COALESCE(SUM(a.quantidade * COALESCE(a.precoAtual, a.precoCompra)), 0) FROM Ativo a WHERE a.carteira = :carteira")
    java.math.BigDecimal calcularValorTotalCarteira(@Param("carteira") Carteira carteira);
}
