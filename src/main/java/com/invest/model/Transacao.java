package com.invest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Transacao - representa uma transação financeira em uma carteira
 * Pode ser compra, venda, recebimento de proventos, etc.
 */
@Entity
@Table(name = "transacoes")
public class Transacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Tipo de transação é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transacao", nullable = false)
    private TipoTransacao tipoTransacao;
    
    @NotBlank(message = "Código do ativo é obrigatório")
    @Column(name = "codigo_ativo", nullable = false, length = 20)
    private String codigoAtivo;
    
    @NotBlank(message = "Nome do ativo é obrigatório")
    @Column(name = "nome_ativo", nullable = false)
    private String nomeAtivo;
    
    @NotNull(message = "Tipo do ativo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ativo", nullable = false)
    private TipoAtivo tipoAtivo;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    @Column(name = "quantidade", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantidade;
    
    @NotNull(message = "Preço unitário é obrigatório")
    @Positive(message = "Preço unitário deve ser positivo")
    @Column(name = "preco_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoUnitario;
    
    @NotNull(message = "Valor total é obrigatório")
    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;
    
    @Column(name = "taxas_corretagem", precision = 15, scale = 2)
    private BigDecimal taxasCorretagem;
    
    @Column(name = "impostos", precision = 15, scale = 2)
    private BigDecimal impostos;
    
    @Column(name = "valor_liquido", precision = 15, scale = 2)
    private BigDecimal valorLiquido;
    
    @Column(name = "data_transacao", nullable = false)
    private LocalDateTime dataTransacao;
    
    @Column(name = "data_liquidacao")
    private LocalDateTime dataLiquidacao;
    
    @Column(name = "observacoes", length = 500)
    private String observacoes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private Carteira carteira;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ativo_id")
    private Ativo ativo;
    
    // Construtores
    public Transacao() {
        this.dataTransacao = LocalDateTime.now();
    }
    
    public Transacao(TipoTransacao tipoTransacao, String codigoAtivo, String nomeAtivo, 
                    TipoAtivo tipoAtivo, BigDecimal quantidade, BigDecimal precoUnitario) {
        this();
        this.tipoTransacao = tipoTransacao;
        this.codigoAtivo = codigoAtivo;
        this.nomeAtivo = nomeAtivo;
        this.tipoAtivo = tipoAtivo;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotal = quantidade.multiply(precoUnitario);
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }
    
    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }
    
    public String getCodigoAtivo() {
        return codigoAtivo;
    }
    
    public void setCodigoAtivo(String codigoAtivo) {
        this.codigoAtivo = codigoAtivo;
    }
    
    public String getNomeAtivo() {
        return nomeAtivo;
    }
    
    public void setNomeAtivo(String nomeAtivo) {
        this.nomeAtivo = nomeAtivo;
    }
    
    public TipoAtivo getTipoAtivo() {
        return tipoAtivo;
    }
    
    public void setTipoAtivo(TipoAtivo tipoAtivo) {
        this.tipoAtivo = tipoAtivo;
    }
    
    public BigDecimal getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
    
    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
    
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
    
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    
    public BigDecimal getTaxasCorretagem() {
        return taxasCorretagem;
    }
    
    public void setTaxasCorretagem(BigDecimal taxasCorretagem) {
        this.taxasCorretagem = taxasCorretagem;
    }
    
    public BigDecimal getImpostos() {
        return impostos;
    }
    
    public void setImpostos(BigDecimal impostos) {
        this.impostos = impostos;
    }
    
    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }
    
    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }
    
    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }
    
    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
    
    public LocalDateTime getDataLiquidacao() {
        return dataLiquidacao;
    }
    
    public void setDataLiquidacao(LocalDateTime dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public Carteira getCarteira() {
        return carteira;
    }
    
    public void setCarteira(Carteira carteira) {
        this.carteira = carteira;
    }
    
    public Ativo getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Ativo ativo) {
        this.ativo = ativo;
    }
    
    // Métodos de cálculo
    public void calcularValorLiquido() {
        BigDecimal valor = valorTotal;
        if (taxasCorretagem != null) {
            valor = valor.subtract(taxasCorretagem);
        }
        if (impostos != null) {
            valor = valor.subtract(impostos);
        }
        this.valorLiquido = valor;
    }
    
    public boolean isCompra() {
        return TipoTransacao.COMPRA.equals(tipoTransacao);
    }
    
    public boolean isVenda() {
        return TipoTransacao.VENDA.equals(tipoTransacao);
    }
    
    public boolean isProvento() {
        return TipoTransacao.PROVENTO.equals(tipoTransacao);
    }
    
    @PrePersist
    @PreUpdate
    public void calcularValores() {
        if (quantidade != null && precoUnitario != null) {
            this.valorTotal = quantidade.multiply(precoUnitario);
        }
        calcularValorLiquido();
    }
}
