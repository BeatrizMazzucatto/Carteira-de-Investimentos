package com.invest.dto;

import com.invest.model.TipoAtivo;
import com.invest.model.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respostas de transações
 */
public class TransacaoResponse {
    
    private Long id;
    private TipoTransacao tipoTransacao;
    private String codigoAtivo;
    private String nomeAtivo;
    private TipoAtivo tipoAtivo;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal valorTotal;
    private BigDecimal taxasCorretagem;
    private BigDecimal impostos;
    private BigDecimal valorLiquido;
    private LocalDateTime dataTransacao;
    private LocalDateTime dataLiquidacao;
    private String observacoes;
    private Long carteiraId;
    private String carteiraNome;
    private Long ativoId;
    
    // Construtores
    public TransacaoResponse() {}
    
    public TransacaoResponse(Long id, TipoTransacao tipoTransacao, String codigoAtivo, String nomeAtivo,
                           TipoAtivo tipoAtivo, BigDecimal quantidade, BigDecimal precoUnitario,
                           BigDecimal valorTotal, BigDecimal taxasCorretagem, BigDecimal impostos,
                           BigDecimal valorLiquido, LocalDateTime dataTransacao, LocalDateTime dataLiquidacao,
                           String observacoes, Long carteiraId, String carteiraNome, Long ativoId) {
        this.id = id;
        this.tipoTransacao = tipoTransacao;
        this.codigoAtivo = codigoAtivo;
        this.nomeAtivo = nomeAtivo;
        this.tipoAtivo = tipoAtivo;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotal = valorTotal;
        this.taxasCorretagem = taxasCorretagem;
        this.impostos = impostos;
        this.valorLiquido = valorLiquido;
        this.dataTransacao = dataTransacao;
        this.dataLiquidacao = dataLiquidacao;
        this.observacoes = observacoes;
        this.carteiraId = carteiraId;
        this.carteiraNome = carteiraNome;
        this.ativoId = ativoId;
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
    
    public Long getAtivoId() {
        return ativoId;
    }
    
    public void setAtivoId(Long ativoId) {
        this.ativoId = ativoId;
    }
}
