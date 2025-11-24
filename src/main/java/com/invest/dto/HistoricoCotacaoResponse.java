package com.invest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para histórico de cotações de um ativo
 * Usado para gerar gráficos de variação no front-end
 */
public class HistoricoCotacaoResponse {
    
    private String codigo;
    private String nome;
    private HistoricoItem[] historico;
    
    public HistoricoCotacaoResponse() {}
    
    public HistoricoCotacaoResponse(String codigo, String nome, HistoricoItem[] historico) {
        this.codigo = codigo;
        this.nome = nome;
        this.historico = historico;
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
    
    public HistoricoItem[] getHistorico() {
        return historico;
    }
    
    public void setHistorico(HistoricoItem[] historico) {
        this.historico = historico;
    }
    
    /**
     * Item do histórico com data/hora e preço
     */
    public static class HistoricoItem {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime dataHora;
        private BigDecimal preco;
        private BigDecimal variacao;
        private BigDecimal variacaoPercentual;
        
        public HistoricoItem() {}
        
        public HistoricoItem(LocalDateTime dataHora, BigDecimal preco, BigDecimal variacao, BigDecimal variacaoPercentual) {
            this.dataHora = dataHora;
            this.preco = preco;
            this.variacao = variacao;
            this.variacaoPercentual = variacaoPercentual;
        }
        
        // Getters e Setters
        public LocalDateTime getDataHora() {
            return dataHora;
        }
        
        public void setDataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
        }
        
        public BigDecimal getPreco() {
            return preco;
        }
        
        public void setPreco(BigDecimal preco) {
            this.preco = preco;
        }
        
        public BigDecimal getVariacao() {
            return variacao;
        }
        
        public void setVariacao(BigDecimal variacao) {
            this.variacao = variacao;
        }
        
        public BigDecimal getVariacaoPercentual() {
            return variacaoPercentual;
        }
        
        public void setVariacaoPercentual(BigDecimal variacaoPercentual) {
            this.variacaoPercentual = variacaoPercentual;
        }
    }
}

