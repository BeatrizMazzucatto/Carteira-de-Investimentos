package com.invest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Investidor - representa um investidor pessoa física
 * Pode ter múltiplas carteiras de investimentos
 */
@Entity
@Table(name = "investidores")
public class Investidor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false)
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 4, message = "Senha deve ter no mínimo 4 caracteres")
    @Column(nullable = false)
    private String senha;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao", nullable = true, insertable = true, updatable = true)
    private LocalDateTime dataAtualizacao;
    
    @OneToMany(mappedBy = "investidor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Carteira> carteiras = new ArrayList<>();
    
    // Construtores
    public Investidor() {
        this.dataCriacao = LocalDateTime.now();
    }
    
    public Investidor(String nome, String email, String senha) {
        this();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
    public List<Carteira> getCarteiras() {
        return carteiras;
    }
    
    public void setCarteiras(List<Carteira> carteiras) {
        this.carteiras = carteiras;
    }
    
    public void adicionarCarteira(Carteira carteira) {
        carteiras.add(carteira);
        carteira.setInvestidor(this);
    }
    
    public void removerCarteira(Carteira carteira) {
        carteiras.remove(carteira);
        carteira.setInvestidor(null);
    }
}
