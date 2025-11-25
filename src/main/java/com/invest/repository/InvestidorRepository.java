package com.invest.repository;

import com.invest.model.Investidor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade Investidor
 */
@Repository
public interface InvestidorRepository extends JpaRepository<Investidor, Long> {
    
    /**
     * Busca investidor por email
     */
    Optional<Investidor> findByEmail(String email);
    
    /**
     * Verifica se existe investidor com o email informado
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca investidores por nome (case insensitive)
     */
    Page<Investidor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
