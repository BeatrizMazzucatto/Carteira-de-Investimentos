package com.invest.service;

import com.invest.model.Investidor;
import com.invest.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service para lógica de negócio dos investidores
 */
@Service
@Transactional
public class InvestidorService {

    @Autowired
    private InvestidorRepository investidorRepository;

    /**
     * Cria um novo investidor
     */
    public Investidor createInvestidor(Investidor investidor) {
        investidor.setDataCriacao(LocalDateTime.now());
        return investidorRepository.save(investidor);
    }

    /**
     * Busca investidor por ID
     */
    public Investidor getInvestidorById(Long id) {
        return investidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investidor não encontrado: " + id));
    }

    /**
     * Lista todos os investidores
     */
    public List<Investidor> getAllInvestidores() {
        return investidorRepository.findAll();
    }

    /**
     * Atualiza um investidor
     */
    public Investidor updateInvestidor(Long id, Investidor investidorAtualizado) {
        Investidor investidor = getInvestidorById(id);
        
        investidor.setNome(investidorAtualizado.getNome());
        investidor.setEmail(investidorAtualizado.getEmail());
        investidor.setSenha(investidorAtualizado.getSenha());
        investidor.setDataAtualizacao(LocalDateTime.now());
        
        return investidorRepository.save(investidor);
    }

    /**
     * Deleta um investidor
     */
    public void deleteInvestidor(Long id) {
        Investidor investidor = getInvestidorById(id);
        investidorRepository.delete(investidor);
    }

    /**
     * Busca investidor por email
     */
    public Optional<Investidor> getInvestidorByEmail(String email) {
        return investidorRepository.findByEmail(email);
    }

    /**
     * Verifica se investidor existe
     */
    public boolean existsById(Long id) {
        return investidorRepository.existsById(id);
    }

    /**
     * Conta total de investidores
     */
    public long countInvestidores() {
        return investidorRepository.count();
    }
}
