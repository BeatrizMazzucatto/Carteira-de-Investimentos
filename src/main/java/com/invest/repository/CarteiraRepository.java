package com.invest.repository;

import com.invest.model.Carteira;
import com.invest.model.Investidor;
import com.invest.model.ObjetivoCarteira;
import com.invest.model.PerfilRisco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Carteira
 */
@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, Long> {
    
    /**
     * Busca todas as carteiras de um investidor
     */
    List<Carteira> findByInvestidor(Investidor investidor);
    
    /**
     * Busca carteiras de um investidor com paginação
     */
    Page<Carteira> findByInvestidor(Investidor investidor, Pageable pageable);
    
    /**
     * Busca carteiras por investidor e objetivo
     */
    List<Carteira> findByInvestidorAndObjetivo(Investidor investidor, ObjetivoCarteira objetivo);
    
    /**
     * Busca carteiras por investidor e perfil de risco
     */
    List<Carteira> findByInvestidorAndPerfilRisco(Investidor investidor, PerfilRisco perfilRisco);
    
    /**
     * Busca carteira por ID e investidor (para validação de propriedade)
     */
    Optional<Carteira> findByIdAndInvestidor(Long id, Investidor investidor);
    
    /**
     * Busca carteiras por nome (case insensitive) de um investidor
     */
    Page<Carteira> findByInvestidorAndNomeContainingIgnoreCase(Investidor investidor, String nome, Pageable pageable);
    
    /**
     * Busca carteira por Google Sheets ID
     */
    Optional<Carteira> findByGoogleSheetsId(String googleSheetsId);
    
    /**
     * Conta o número de carteiras de um investidor
     */
    long countByInvestidor(Investidor investidor);
    
    /**
     * Busca carteiras que precisam de atualização de preços
     */
    @Query("SELECT c FROM Carteira c WHERE c.dataAtualizacao < :dataLimite OR c.dataAtualizacao IS NULL")
    List<Carteira> findCarteirasParaAtualizacao(@Param("dataLimite") java.time.LocalDateTime dataLimite);
}
