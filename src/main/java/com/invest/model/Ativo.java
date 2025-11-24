package com.invest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Entidade Ativo - representa um ativo financeiro dentro de uma carteira
 */
@Entity
@Table(name = "ativos")
public class Ativo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Código do ativo é obrigatório")
    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;
    
    @NotBlank(message = "Nome do ativo é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;
    
    @NotNull(message = "Tipo do ativo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoAtivo tipo;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    @Column(name = "quantidade", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantidade;
    
    @NotNull(message = "Preço de compra é obrigatório")
    @Positive(message = "Preço de compra deve ser positivo")
    @Column(name = "preco_compra", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoCompra;
    
    @Column(name = "preco_atual", precision = 15, scale = 2)
    private BigDecimal precoAtual;
    
    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private Carteira carteira;
    
    // Construtores
    public Ativo() {
        this.dataCompra = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public Ativo(String codigo, String nome, TipoAtivo tipo, BigDecimal quantidade, BigDecimal precoCompra) {
        this();
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.precoCompra = precoCompra;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public TipoAtivo getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoAtivo tipo) {
        this.tipo = tipo;
    }
    
    public BigDecimal getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
    
    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }
    
    public void setPrecoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
    }
    
    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }
    
    public void setPrecoAtual(BigDecimal precoAtual) {
        this.precoAtual = precoAtual;
    }
    
    public LocalDateTime getDataCompra() {
        return dataCompra;
    }
    
    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
    public Carteira getCarteira() {
        return carteira;
    }
    
    public void setCarteira(Carteira carteira) {
        this.carteira = carteira;
    }
    
    // Métodos de cálculo
    public BigDecimal getValorTotalCompra() {
        return quantidade.multiply(precoCompra);
    }
    
    public BigDecimal getValorTotalAtual() {
        if (precoAtual != null) {
            return quantidade.multiply(precoAtual);
        }
        return getValorTotalCompra();
    }
    
    /**
     * Calcula a variação percentual do ativo em relação ao seu próprio preço de compra.
     * Fórmula: ((precoAtual - precoCompra) / precoCompra) * 100
     */
    public BigDecimal getVariacaoPercentual() {
        if (precoAtual != null && precoCompra != null && precoCompra.compareTo(BigDecimal.ZERO) > 0) {
            return precoAtual.subtract(precoCompra)
                    .divide(precoCompra, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}