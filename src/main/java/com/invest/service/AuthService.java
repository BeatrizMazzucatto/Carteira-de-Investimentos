package com.invest.service;

import com.invest.model.Investidor;
import com.invest.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service para autenticação e autorização com JWT
 */
@Service
public class AuthService {

    @Autowired
    private InvestidorService investidorService;

    @Autowired
    private JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Autentica um investidor e retorna um token JWT
     */
    public AuthResult authenticate(String email, String senha) {
        // Busca o investidor por email (case-insensitive)
        Optional<Investidor> investidorOpt = investidorService.getInvestidorByEmail(email);
        
        if (investidorOpt.isEmpty()) {
            return new AuthResult(false, "Email ou senha incorretos", null, null);
        }

        Investidor investidor = investidorOpt.get();
        
        // Verifica a senha
        String senhaArmazenada = investidor.getSenha();
        boolean senhaValida = false;
        
        if (senhaArmazenada == null || senhaArmazenada.isEmpty()) {
            return new AuthResult(false, "Email ou senha incorretos", null, null);
        }
        
        // Se a senha começa com $2a$ ou $2b$, está hasheada com BCrypt
        if (senhaArmazenada.startsWith("$2a$") || senhaArmazenada.startsWith("$2b$")) {
            // Senha hasheada, usa BCrypt para comparar
            senhaValida = passwordEncoder.matches(senha, senhaArmazenada);
        } else {
            // Senha em texto plano (migração de dados antigos)
            senhaValida = senhaArmazenada.equals(senha);
            
            // Se a senha está correta, faz o hash e atualiza no banco
            if (senhaValida) {
                String senhaHash = passwordEncoder.encode(senha);
                investidor.setSenha(senhaHash);
                investidorService.updateInvestidor(investidor.getId(), investidor);
            }
        }

        if (!senhaValida) {
            return new AuthResult(false, "Email ou senha incorretos", null, null);
        }

        // Gera token JWT
        String token = jwtUtil.generateToken(investidor.getId(), investidor.getEmail());
        
        return new AuthResult(true, "Login realizado com sucesso", token, investidor);
    }

    /**
     * Valida um token JWT
     */
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtém o ID do investidor a partir do token
     */
    public Long getInvestidorIdFromToken(String token) {
        try {
            return jwtUtil.getInvestidorIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obtém o email do investidor a partir do token
     */
    public String getEmailFromToken(String token) {
        try {
            return jwtUtil.getEmailFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Faz hash de uma senha
     */
    public String encodePassword(String senha) {
        return passwordEncoder.encode(senha);
    }

    /**
     * Classe para retornar resultado da autenticação
     */
    public static class AuthResult {
        private final boolean sucesso;
        private final String mensagem;
        private final String token;
        private final Investidor investidor;

        public AuthResult(boolean sucesso, String mensagem, String token, Investidor investidor) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.token = token;
            this.investidor = investidor;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public String getToken() {
            return token;
        }

        public Investidor getInvestidor() {
            return investidor;
        }
    }
}

