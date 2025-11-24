package com.invest.dto;

import com.invest.model.TipoAtivo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respostas de ativos
 */
public class AtivoResponse {
    
    private Long id;
    private String codigo;
    private String nome;
    private TipoAtivo tipo;
    private BigDecimal quantidade;
    private BigDecimal precoCompra;
    private BigDecimal precoAtual;
    private LocalDateTime dataCompra;
    private LocalDateTime dataAtualizacao;
    private Long carteiraId;
    private String carteiraNome;
    private BigDecimal valorTotalCompra;
    private BigDecimal valorTotalAtual;
    private BigDecimal variacaoPercentual;
    
    // Construtores
    public AtivoResponse() {}
    
    public AtivoResponse(Long id, String codigo, String nome, TipoAtivo tipo, BigDecimal quantidade,
                        BigDecimal precoCompra, BigDecimal precoAtual, LocalDateTime dataCompra,
                        LocalDateTime dataAtualizacao, Long carteiraId, String carteiraNome) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.precoCompra = precoCompra;
        this.precoAtual = precoAtual;
        this.dataCompra = dataCompra;
        this.dataAtualizacao = dataAtualizacao;
        this.carteiraId = carteiraId;
        this.carteiraNome = carteiraNome;
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
    
    public Long getCarteiraId() {
        return carteiraId;
    }
    
    public void setCarteiraId(Long carteiraId) {
        this.carteiraId = carteiraId;
    }
    
    public String getCarteiraNome() {
        return carteiraNome;
    }
    
    public void setCarteiraNome(String carteiraNome) {
        this.carteiraNome = carteiraNome;
    }
    
    public BigDecimal getValorTotalCompra() {
        return valorTotalCompra;
    }
    
    public void setValorTotalCompra(BigDecimal valorTotalCompra) {
        this.valorTotalCompra = valorTotalCompra;
    }
    
    public BigDecimal getValorTotalAtual() {
        return valorTotalAtual;
    }
    
    public void setValorTotalAtual(BigDecimal valorTotalAtual) {
        this.valorTotalAtual = valorTotalAtual;
    }
    
    public BigDecimal getVariacaoPercentual() {
        return variacaoPercentual;
    }
    
    public void setVariacaoPercentual(BigDecimal variacaoPercentual) {
        this.variacaoPercentual = variacaoPercentual;
    }
}
