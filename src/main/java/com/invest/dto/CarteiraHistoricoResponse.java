package com.invest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para histórico de cotações cruzado com carteiras
 * Mostra a variação dos ativos nas carteiras do investidor
 */
public class CarteiraHistoricoResponse {
    
    private Long carteiraId;
    private String carteiraNome;
    private String investidorNome;
    private List<AtivoHistorico> ativos;
    private ResumoCarteira resumo;
    
    public CarteiraHistoricoResponse() {}
    
    // Getters e Setters
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
    
    public String getInvestidorNome() {
        return investidorNome;
    }
    
    public void setInvestidorNome(String investidorNome) {
        this.investidorNome = investidorNome;
    }
    
    public List<AtivoHistorico> getAtivos() {
        return ativos;
    }
    
    public void setAtivos(List<AtivoHistorico> ativos) {
        this.ativos = ativos;
    }
    
    public ResumoCarteira getResumo() {
        return resumo;
    }
    
    public void setResumo(ResumoCarteira resumo) {
        this.resumo = resumo;
    }
    
    /**
     * Histórico de um ativo específico na carteira
     */
    public static class AtivoHistorico {
        private String codigo;
        private String nome;
        private BigDecimal quantidade;
        private BigDecimal precoMedioCompra;
        private HistoricoItem[] historico;
        private BigDecimal valorAtual;
        private BigDecimal valorInvestido;
        private BigDecimal ganhoPerda;
        private BigDecimal ganhoPerdaPercentual;
        
        public AtivoHistorico() {}
        
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
        
        public BigDecimal getQuantidade() {
            return quantidade;
        }
        
        public void setQuantidade(BigDecimal quantidade) {
            this.quantidade = quantidade;
        }
        
        public BigDecimal getPrecoMedioCompra() {
            return precoMedioCompra;
        }
        
        public void setPrecoMedioCompra(BigDecimal precoMedioCompra) {
            this.precoMedioCompra = precoMedioCompra;
        }
        
        public HistoricoItem[] getHistorico() {
            return historico;
        }
        
        public void setHistorico(HistoricoItem[] historico) {
            this.historico = historico;
        }
        
        public BigDecimal getValorAtual() {
            return valorAtual;
        }
        
        public void setValorAtual(BigDecimal valorAtual) {
            this.valorAtual = valorAtual;
        }
        
        public BigDecimal getValorInvestido() {
            return valorInvestido;
        }
        
        public void setValorInvestido(BigDecimal valorInvestido) {
            this.valorInvestido = valorInvestido;
        }
        
        public BigDecimal getGanhoPerda() {
            return ganhoPerda;
        }
        
        public void setGanhoPerda(BigDecimal ganhoPerda) {
            this.ganhoPerda = ganhoPerda;
        }
        
        public BigDecimal getGanhoPerdaPercentual() {
            return ganhoPerdaPercentual;
        }
        
        public void setGanhoPerdaPercentual(BigDecimal ganhoPerdaPercentual) {
            this.ganhoPerdaPercentual = ganhoPerdaPercentual;
        }
    }
    
    /**
     * Item do histórico com data/hora e preço
     */
    public static class HistoricoItem {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime dataHora;
        private BigDecimal preco;
        private BigDecimal valorTotal;
        private BigDecimal ganhoPerda;
        private BigDecimal ganhoPerdaPercentual;
        
        public HistoricoItem() {}
        
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
        
        public BigDecimal getValorTotal() {
            return valorTotal;
        }
        
        public void setValorTotal(BigDecimal valorTotal) {
            this.valorTotal = valorTotal;
        }
        
        public BigDecimal getGanhoPerda() {
            return ganhoPerda;
        }
        
        public void setGanhoPerda(BigDecimal ganhoPerda) {
            this.ganhoPerda = ganhoPerda;
        }
        
        public BigDecimal getGanhoPerdaPercentual() {
            return ganhoPerdaPercentual;
        }
        
        public void setGanhoPerdaPercentual(BigDecimal ganhoPerdaPercentual) {
            this.ganhoPerdaPercentual = ganhoPerdaPercentual;
        }
    }
    
    /**
     * Resumo da carteira com valores totais
     */
    public static class ResumoCarteira {
        private BigDecimal valorTotalInvestido;
        private BigDecimal valorTotalAtual;
        private BigDecimal ganhoPerdaTotal;
        private BigDecimal ganhoPerdaPercentualTotal;
        
        public ResumoCarteira() {}
        
        // Getters e Setters
        public BigDecimal getValorTotalInvestido() {
            return valorTotalInvestido;
        }
        
        public void setValorTotalInvestido(BigDecimal valorTotalInvestido) {
            this.valorTotalInvestido = valorTotalInvestido;
        }
        
        public BigDecimal getValorTotalAtual() {
            return valorTotalAtual;
        }
        
        public void setValorTotalAtual(BigDecimal valorTotalAtual) {
            this.valorTotalAtual = valorTotalAtual;
        }
        
        public BigDecimal getGanhoPerdaTotal() {
            return ganhoPerdaTotal;
        }
        
        public void setGanhoPerdaTotal(BigDecimal ganhoPerdaTotal) {
            this.ganhoPerdaTotal = ganhoPerdaTotal;
        }
        
        public BigDecimal getGanhoPerdaPercentualTotal() {
            return ganhoPerdaPercentualTotal;
        }
        
        public void setGanhoPerdaPercentualTotal(BigDecimal ganhoPerdaPercentualTotal) {
            this.ganhoPerdaPercentualTotal = ganhoPerdaPercentualTotal;
        }
    }
}

