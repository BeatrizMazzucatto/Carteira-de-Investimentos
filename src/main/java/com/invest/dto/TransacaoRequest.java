package com.invest.dto;

import com.invest.model.TipoAtivo;
import com.invest.model.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para requisições de criação e atualização de transações
 */
public class TransacaoRequest {
    
    @NotNull(message = "Tipo de transação é obrigatório")
    private TipoTransacao tipoTransacao;
    
    @NotBlank(message = "Código do ativo é obrigatório")
    private String codigoAtivo;
    
    @NotBlank(message = "Nome do ativo é obrigatório")
    private String nomeAtivo;
    
    @NotNull(message = "Tipo do ativo é obrigatório")
    private TipoAtivo tipoAtivo;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private BigDecimal quantidade;
    
    @NotNull(message = "Preço unitário é obrigatório")
    @Positive(message = "Preço unitário deve ser positivo")
    private BigDecimal precoUnitario;
    
    private BigDecimal taxasCorretagem;
    private BigDecimal impostos;
    private LocalDateTime dataTransacao;
    private LocalDateTime dataLiquidacao;
    private String observacoes;
    
    // Construtores
    public TransacaoRequest() {}
    
    public TransacaoRequest(TipoTransacao tipoTransacao, String codigoAtivo, String nomeAtivo, 
                           TipoAtivo tipoAtivo, BigDecimal quantidade, BigDecimal precoUnitario) {
        this.tipoTransacao = tipoTransacao;
        this.codigoAtivo = codigoAtivo;
        this.nomeAtivo = nomeAtivo;
        this.tipoAtivo = tipoAtivo;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }
    
    // Getters e Setters
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
}
