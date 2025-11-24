package com.invest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Carteira - representa uma carteira de investimentos
 * Cada investidor pode ter múltiplas carteiras com objetivos distintos
 */
@Entity
@Table(name = "carteiras")
public class Carteira {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome da carteira é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false)
    private String nome;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String descricao;
    
    @NotNull(message = "Objetivo da carteira é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjetivoCarteira objetivo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "prazo", nullable = true)
    private PrazoCarteira prazo;
    
    @NotNull(message = "Perfil de risco é obrigatório")
    @Convert(converter = PerfilRiscoConverter.class)
    @Column(name = "perfil_risco", nullable = false)
    private PerfilRisco perfilRisco;
    
    @Column(name = "valor_inicial", precision = 15, scale = 2)
    private BigDecimal valorInicial;
    
    @Column(name = "valor_atual", precision = 15, scale = 2)
    private BigDecimal valorAtual;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "google_sheets_id")
    private String googleSheetsId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investidor_id", nullable = false)
    private Investidor investidor;
    
    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ativo> ativos = new ArrayList<>();
    
    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoes = new ArrayList<>();
    
    // Construtores
    public Carteira() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public Carteira(String nome, ObjetivoCarteira objetivo, PerfilRisco perfilRisco) {
        this();
        this.nome = nome;
        this.objetivo = objetivo;
        this.perfilRisco = perfilRisco;
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
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public ObjetivoCarteira getObjetivo() {
        return objetivo;
    }
    
    public void setObjetivo(ObjetivoCarteira objetivo) {
        this.objetivo = objetivo;
    }
    
    public PrazoCarteira getPrazo() {
        return prazo;
    }
    
    public void setPrazo(PrazoCarteira prazo) {
        this.prazo = prazo;
    }
    
    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }
    
    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }
    
    public BigDecimal getValorInicial() {
        return valorInicial;
    }
    
    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }
    
    public BigDecimal getValorAtual() {
        return valorAtual;
    }
    
    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
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
    
    public String getGoogleSheetsId() {
        return googleSheetsId;
    }
    
    public void setGoogleSheetsId(String googleSheetsId) {
        this.googleSheetsId = googleSheetsId;
    }
    
    public Investidor getInvestidor() {
        return investidor;
    }
    
    public void setInvestidor(Investidor investidor) {
        this.investidor = investidor;
    }
    
    public List<Ativo> getAtivos() {
        return ativos;
    }
    
    public void setAtivos(List<Ativo> ativos) {
        this.ativos = ativos;
    }
    
    public void adicionarAtivo(Ativo ativo) {
        ativos.add(ativo);
        ativo.setCarteira(this);
    }
    
    public void removerAtivo(Ativo ativo) {
        ativos.remove(ativo);
        ativo.setCarteira(null);
    }
    
    public List<Transacao> getTransacoes() {
        return transacoes;
    }
    
    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
    
    public void adicionarTransacao(Transacao transacao) {
        transacoes.add(transacao);
        transacao.setCarteira(this);
    }
    
    public void removerTransacao(Transacao transacao) {
        transacoes.remove(transacao);
        transacao.setCarteira(null);
    }
    
    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
