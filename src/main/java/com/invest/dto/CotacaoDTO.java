package com.invest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de cotações em tempo real
 */
public class CotacaoDTO {
    
    private String codigo;
    private String nome;
    private BigDecimal precoAtual;
    private BigDecimal variacao; // Variação percentual do dia
    private BigDecimal variacaoCalculada; // Variação calculada em relação à última cotação
    private BigDecimal precoAbertura;
    private BigDecimal precoMaximo;
    private BigDecimal precoMinimo;
    private BigDecimal volume;
    private LocalDateTime dataHora;
    
    // Construtores
    public CotacaoDTO() {}
    
    public CotacaoDTO(String codigo, BigDecimal precoAtual, BigDecimal variacao) {
        this.codigo = codigo;
        this.precoAtual = precoAtual;
        this.variacao = variacao;
        this.dataHora = LocalDateTime.now();
    }
    
    // Getters e Setters
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
    
    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }
    
    public void setPrecoAtual(BigDecimal precoAtual) {
        this.precoAtual = precoAtual;
    }
    
    public BigDecimal getVariacao() {
        return variacao;
    }
    
    public void setVariacao(BigDecimal variacao) {
        this.variacao = variacao;
    }
    
    public BigDecimal getVariacaoCalculada() {
        return variacaoCalculada;
    }
    
    public void setVariacaoCalculada(BigDecimal variacaoCalculada) {
        this.variacaoCalculada = variacaoCalculada;
    }
    
    public BigDecimal getPrecoAbertura() {
        return precoAbertura;
    }
    
    public void setPrecoAbertura(BigDecimal precoAbertura) {
        this.precoAbertura = precoAbertura;
    }
    
    public BigDecimal getPrecoMaximo() {
        return precoMaximo;
    }
    
    public void setPrecoMaximo(BigDecimal precoMaximo) {
        this.precoMaximo = precoMaximo;
    }
    
    public BigDecimal getPrecoMinimo() {
        return precoMinimo;
    }
    
    public void setPrecoMinimo(BigDecimal precoMinimo) {
        this.precoMinimo = precoMinimo;
    }
    
    public BigDecimal getVolume() {
        return volume;
    }
    
    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
