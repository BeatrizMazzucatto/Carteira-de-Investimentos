package com.invest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para relatório consolidado da empresa
 * Retorna dados agregados de todos os investidores para processamento no front-end
 */
public class RelatorioEmpresaResponse {
    
    // Metadados do Relatório
    private LocalDateTime dataGeracao;
    private String versao;
    
    // Estatísticas Gerais Consolidadas
    private Integer totalInvestidores;
    private Integer totalCarteiras;
    private Integer totalAtivos;
    private Integer totalTransacoes;
    private BigDecimal valorTotalInvestido;
    private BigDecimal valorTotalAtual;
    private BigDecimal rentabilidadeTotal;
    private BigDecimal rentabilidadePercentual;
    
    // Resumo por Investidor
    private List<InvestidorResumo> investidores;
    
    // Estatísticas Consolidadas por Tipo de Ativo
    // Usa String como chave para facilitar uso no front-end
    private Map<String, EstatisticaTipoAtivo> estatisticasPorTipo;
    
    // Transações Recentes (de todos os investidores)
    private List<TransacaoResumo> transacoesRecentes;
    
    // Classe interna para resumo de investidor
    public static class InvestidorResumo {
        private Long id;
        private String nome;
        private String email;
        private Integer totalCarteiras;
        private Integer totalAtivos;
        private Integer totalTransacoes;
        private BigDecimal valorTotalInvestido;
        private BigDecimal valorTotalAtual;
        private BigDecimal rentabilidade;
        private BigDecimal rentabilidadePercentual;
        private LocalDateTime dataCriacao;
        
        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public Integer getTotalCarteiras() { return totalCarteiras; }
        public void setTotalCarteiras(Integer totalCarteiras) { this.totalCarteiras = totalCarteiras; }
        
        public Integer getTotalAtivos() { return totalAtivos; }
        public void setTotalAtivos(Integer totalAtivos) { this.totalAtivos = totalAtivos; }
        
        public Integer getTotalTransacoes() { return totalTransacoes; }
        public void setTotalTransacoes(Integer totalTransacoes) { this.totalTransacoes = totalTransacoes; }
        
        public BigDecimal getValorTotalInvestido() { return valorTotalInvestido; }
        public void setValorTotalInvestido(BigDecimal valorTotalInvestido) { this.valorTotalInvestido = valorTotalInvestido; }
        
        public BigDecimal getValorTotalAtual() { return valorTotalAtual; }
        public void setValorTotalAtual(BigDecimal valorTotalAtual) { this.valorTotalAtual = valorTotalAtual; }
        
        public BigDecimal getRentabilidade() { return rentabilidade; }
        public void setRentabilidade(BigDecimal rentabilidade) { this.rentabilidade = rentabilidade; }
        
        public BigDecimal getRentabilidadePercentual() { return rentabilidadePercentual; }
        public void setRentabilidadePercentual(BigDecimal rentabilidadePercentual) { this.rentabilidadePercentual = rentabilidadePercentual; }
        
        public LocalDateTime getDataCriacao() { return dataCriacao; }
        public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    }
    
    // Classe interna para estatísticas por tipo de ativo
    public static class EstatisticaTipoAtivo {
        private String tipo; // String para facilitar uso no front-end
        private Integer quantidade;
        private BigDecimal valorTotalInvestido;
        private BigDecimal valorTotalAtual;
        private BigDecimal rentabilidade;
        private BigDecimal rentabilidadePercentual;
        
        // Getters e Setters
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
        
        public BigDecimal getValorTotalInvestido() { return valorTotalInvestido; }
        public void setValorTotalInvestido(BigDecimal valorTotalInvestido) { this.valorTotalInvestido = valorTotalInvestido; }
        
        public BigDecimal getValorTotalAtual() { return valorTotalAtual; }
        public void setValorTotalAtual(BigDecimal valorTotalAtual) { this.valorTotalAtual = valorTotalAtual; }
        
        public BigDecimal getRentabilidade() { return rentabilidade; }
        public void setRentabilidade(BigDecimal rentabilidade) { this.rentabilidade = rentabilidade; }
        
        public BigDecimal getRentabilidadePercentual() { return rentabilidadePercentual; }
        public void setRentabilidadePercentual(BigDecimal rentabilidadePercentual) { this.rentabilidadePercentual = rentabilidadePercentual; }
    }
    
    // Classe interna para resumo de transação
    public static class TransacaoResumo {
        private Long id;
        private String tipoTransacao;
        private String codigoAtivo;
        private String nomeAtivo;
        private String tipoAtivo;
        private BigDecimal quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal valorTotal;
        private LocalDateTime dataTransacao;
        private String carteiraNome;
        private Long carteiraId;
        private String investidorNome;
        private Long investidorId;
        
        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getTipoTransacao() { return tipoTransacao; }
        public void setTipoTransacao(String tipoTransacao) { this.tipoTransacao = tipoTransacao; }
        
        public String getCodigoAtivo() { return codigoAtivo; }
        public void setCodigoAtivo(String codigoAtivo) { this.codigoAtivo = codigoAtivo; }
        
        public String getNomeAtivo() { return nomeAtivo; }
        public void setNomeAtivo(String nomeAtivo) { this.nomeAtivo = nomeAtivo; }
        
        public String getTipoAtivo() { return tipoAtivo; }
        public void setTipoAtivo(String tipoAtivo) { this.tipoAtivo = tipoAtivo; }
        
        public BigDecimal getQuantidade() { return quantidade; }
        public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
        
        public BigDecimal getPrecoUnitario() { return precoUnitario; }
        public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
        
        public BigDecimal getValorTotal() { return valorTotal; }
        public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
        
        public LocalDateTime getDataTransacao() { return dataTransacao; }
        public void setDataTransacao(LocalDateTime dataTransacao) { this.dataTransacao = dataTransacao; }
        
        public String getCarteiraNome() { return carteiraNome; }
        public void setCarteiraNome(String carteiraNome) { this.carteiraNome = carteiraNome; }
        
        public Long getCarteiraId() { return carteiraId; }
        public void setCarteiraId(Long carteiraId) { this.carteiraId = carteiraId; }
        
        public String getInvestidorNome() { return investidorNome; }
        public void setInvestidorNome(String investidorNome) { this.investidorNome = investidorNome; }
        
        public Long getInvestidorId() { return investidorId; }
        public void setInvestidorId(Long investidorId) { this.investidorId = investidorId; }
    }
    
    // Getters e Setters principais
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }
    
    public String getVersao() { return versao; }
    public void setVersao(String versao) { this.versao = versao; }
    
    public Integer getTotalInvestidores() { return totalInvestidores; }
    public void setTotalInvestidores(Integer totalInvestidores) { this.totalInvestidores = totalInvestidores; }
    
    public Integer getTotalCarteiras() { return totalCarteiras; }
    public void setTotalCarteiras(Integer totalCarteiras) { this.totalCarteiras = totalCarteiras; }
    
    public Integer getTotalAtivos() { return totalAtivos; }
    public void setTotalAtivos(Integer totalAtivos) { this.totalAtivos = totalAtivos; }
    
    public Integer getTotalTransacoes() { return totalTransacoes; }
    public void setTotalTransacoes(Integer totalTransacoes) { this.totalTransacoes = totalTransacoes; }
    
    public BigDecimal getValorTotalInvestido() { return valorTotalInvestido; }
    public void setValorTotalInvestido(BigDecimal valorTotalInvestido) { this.valorTotalInvestido = valorTotalInvestido; }
    
    public BigDecimal getValorTotalAtual() { return valorTotalAtual; }
    public void setValorTotalAtual(BigDecimal valorTotalAtual) { this.valorTotalAtual = valorTotalAtual; }
    
    public BigDecimal getRentabilidadeTotal() { return rentabilidadeTotal; }
    public void setRentabilidadeTotal(BigDecimal rentabilidadeTotal) { this.rentabilidadeTotal = rentabilidadeTotal; }
    
    public BigDecimal getRentabilidadePercentual() { return rentabilidadePercentual; }
    public void setRentabilidadePercentual(BigDecimal rentabilidadePercentual) { this.rentabilidadePercentual = rentabilidadePercentual; }
    
    public List<InvestidorResumo> getInvestidores() { return investidores; }
    public void setInvestidores(List<InvestidorResumo> investidores) { this.investidores = investidores; }
    
    public Map<String, EstatisticaTipoAtivo> getEstatisticasPorTipo() { return estatisticasPorTipo; }
    public void setEstatisticasPorTipo(Map<String, EstatisticaTipoAtivo> estatisticasPorTipo) { this.estatisticasPorTipo = estatisticasPorTipo; }
    
    public List<TransacaoResumo> getTransacoesRecentes() { return transacoesRecentes; }
    public void setTransacoesRecentes(List<TransacaoResumo> transacoesRecentes) { this.transacoesRecentes = transacoesRecentes; }
}

