package com.invest.service;

import com.invest.model.Investidor;
import com.invest.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Cria um novo investidor (com hash de senha)
     */
    public Investidor createInvestidor(Investidor investidor) {
        investidor.setDataCriacao(LocalDateTime.now());
        // Faz hash da senha antes de salvar (se ainda não estiver hasheada)
        if (investidor.getSenha() != null && 
            !investidor.getSenha().startsWith("$2a$") && 
            !investidor.getSenha().startsWith("$2b$")) {
            String senhaHash = passwordEncoder.encode(investidor.getSenha());
            investidor.setSenha(senhaHash);
        }
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
        
        // Se a senha foi fornecida e não está hasheada, faz hash antes de salvar
        if (investidorAtualizado.getSenha() != null) {
            String senha = investidorAtualizado.getSenha();
            // Verifica se a senha já está hasheada (começa com $2a$ ou $2b$)
            if (!senha.startsWith("$2a$") && !senha.startsWith("$2b$")) {
                String senhaHash = passwordEncoder.encode(senha);
                investidor.setSenha(senhaHash);
            } else {
                // Se já está hasheada, mantém como está
                investidor.setSenha(senha);
            }
        }
        
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
     * Busca investidor por email (case-insensitive)
     */
    public Optional<Investidor> getInvestidorByEmail(String email) {
        // Normaliza email e busca (case-insensitive se o repository suportar)
        String emailNormalizado = email != null ? email.toLowerCase().trim() : "";
        return investidorRepository.findByEmailIgnoreCase(emailNormalizado);
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
