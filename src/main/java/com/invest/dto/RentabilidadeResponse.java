package com.invest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de cálculos de rentabilidade
 */
public class RentabilidadeResponse {
    
    private Long ativoId;
    private String codigoAtivo;
    private String nomeAtivo;
    
    // Valores de Investimento
    private BigDecimal valorTotalInvestido;
    private BigDecimal precoMedioCompra;
    private BigDecimal quantidadeAtual;
    private BigDecimal valorTotalCompras;
    private BigDecimal valorTotalVendas;
    private BigDecimal valorTotalProventos;
    
    // Valores Atuais
    private BigDecimal precoAtual;
    private BigDecimal valorAtualMercado;
    private BigDecimal valorAtualComProventos;
    
    // Cálculos de Rentabilidade
    private BigDecimal rentabilidadeBruta;
    private BigDecimal rentabilidadeLiquida;
    private BigDecimal rentabilidadePercentualBruta;
    private BigDecimal rentabilidadePercentualLiquida;
    private BigDecimal rentabilidadePercentualAnual;
    
    // Custos e Taxas
    private BigDecimal totalTaxasCorretagem;
    private BigDecimal totalImpostos;
    private BigDecimal totalCustos;
    
    // Métricas Adicionais
    private BigDecimal variacaoPercentual;
    private BigDecimal variacaoValor;
    private BigDecimal dividendYield;
    private BigDecimal precoTeto;
    private BigDecimal precoSuporte;
    
    // Datas
    private LocalDateTime dataPrimeiraCompra;
    private LocalDateTime dataUltimaTransacao;
    private LocalDateTime dataAtualizacao;
    
    // Construtores
    public RentabilidadeResponse() {}
    
    public RentabilidadeResponse(Long ativoId, String codigoAtivo, String nomeAtivo) {
        this.ativoId = ativoId;
        this.codigoAtivo = codigoAtivo;
        this.nomeAtivo = nomeAtivo;
    }
    
    // Getters e Setters
    public Long getAtivoId() {
        return ativoId;
    }
    
    public void setAtivoId(Long ativoId) {
        this.ativoId = ativoId;
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
    
    public BigDecimal getValorTotalInvestido() {
        return valorTotalInvestido;
    }
    
    public void setValorTotalInvestido(BigDecimal valorTotalInvestido) {
        this.valorTotalInvestido = valorTotalInvestido;
    }
    
    public BigDecimal getPrecoMedioCompra() {
        return precoMedioCompra;
    }
    
    public void setPrecoMedioCompra(BigDecimal precoMedioCompra) {
        this.precoMedioCompra = precoMedioCompra;
    }
    
    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }
    
    public void setQuantidadeAtual(BigDecimal quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }
    
    public BigDecimal getValorTotalCompras() {
        return valorTotalCompras;
    }
    
    public void setValorTotalCompras(BigDecimal valorTotalCompras) {
        this.valorTotalCompras = valorTotalCompras;
    }
    
    public BigDecimal getValorTotalVendas() {
        return valorTotalVendas;
    }
    
    public void setValorTotalVendas(BigDecimal valorTotalVendas) {
        this.valorTotalVendas = valorTotalVendas;
    }
    
    public BigDecimal getValorTotalProventos() {
        return valorTotalProventos;
    }
    
    public void setValorTotalProventos(BigDecimal valorTotalProventos) {
        this.valorTotalProventos = valorTotalProventos;
    }
    
    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }
    
    public void setPrecoAtual(BigDecimal precoAtual) {
        this.precoAtual = precoAtual;
    }
    
    public BigDecimal getValorAtualMercado() {
        return valorAtualMercado;
    }
    
    public void setValorAtualMercado(BigDecimal valorAtualMercado) {
        this.valorAtualMercado = valorAtualMercado;
    }
    
    public BigDecimal getValorAtualComProventos() {
        return valorAtualComProventos;
    }
    
    public void setValorAtualComProventos(BigDecimal valorAtualComProventos) {
        this.valorAtualComProventos = valorAtualComProventos;
    }
    
    public BigDecimal getRentabilidadeBruta() {
        return rentabilidadeBruta;
    }
    
    public void setRentabilidadeBruta(BigDecimal rentabilidadeBruta) {
        this.rentabilidadeBruta = rentabilidadeBruta;
    }
    
    public BigDecimal getRentabilidadeLiquida() {
        return rentabilidadeLiquida;
    }
    
    public void setRentabilidadeLiquida(BigDecimal rentabilidadeLiquida) {
        this.rentabilidadeLiquida = rentabilidadeLiquida;
    }
    
    public BigDecimal getRentabilidadePercentualBruta() {
        return rentabilidadePercentualBruta;
    }
    
    public void setRentabilidadePercentualBruta(BigDecimal rentabilidadePercentualBruta) {
        this.rentabilidadePercentualBruta = rentabilidadePercentualBruta;
    }
    
    public BigDecimal getRentabilidadePercentualLiquida() {
        return rentabilidadePercentualLiquida;
    }
    
    public void setRentabilidadePercentualLiquida(BigDecimal rentabilidadePercentualLiquida) {
        this.rentabilidadePercentualLiquida = rentabilidadePercentualLiquida;
    }
    
    public BigDecimal getRentabilidadePercentualAnual() {
        return rentabilidadePercentualAnual;
    }
    
    public void setRentabilidadePercentualAnual(BigDecimal rentabilidadePercentualAnual) {
        this.rentabilidadePercentualAnual = rentabilidadePercentualAnual;
    }
    
    public BigDecimal getTotalTaxasCorretagem() {
        return totalTaxasCorretagem;
    }
    
    public void setTotalTaxasCorretagem(BigDecimal totalTaxasCorretagem) {
        this.totalTaxasCorretagem = totalTaxasCorretagem;
    }
    
    public BigDecimal getTotalImpostos() {
        return totalImpostos;
    }
    
    public void setTotalImpostos(BigDecimal totalImpostos) {
        this.totalImpostos = totalImpostos;
    }
    
    public BigDecimal getTotalCustos() {
        return totalCustos;
    }
    
    public void setTotalCustos(BigDecimal totalCustos) {
        this.totalCustos = totalCustos;
    }
    
    public BigDecimal getVariacaoPercentual() {
        return variacaoPercentual;
    }
    
    public void setVariacaoPercentual(BigDecimal variacaoPercentual) {
        this.variacaoPercentual = variacaoPercentual;
    }
    
    public BigDecimal getVariacaoValor() {
        return variacaoValor;
    }
    
    public void setVariacaoValor(BigDecimal variacaoValor) {
        this.variacaoValor = variacaoValor;
    }
    
    public BigDecimal getDividendYield() {
        return dividendYield;
    }
    
    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }
    
    public BigDecimal getPrecoTeto() {
        return precoTeto;
    }
    
    public void setPrecoTeto(BigDecimal precoTeto) {
        this.precoTeto = precoTeto;
    }
    
    public BigDecimal getPrecoSuporte() {
        return precoSuporte;
    }
    
    public void setPrecoSuporte(BigDecimal precoSuporte) {
        this.precoSuporte = precoSuporte;
    }
    
    public LocalDateTime getDataPrimeiraCompra() {
        return dataPrimeiraCompra;
    }
    
    public void setDataPrimeiraCompra(LocalDateTime dataPrimeiraCompra) {
        this.dataPrimeiraCompra = dataPrimeiraCompra;
    }
    
    public LocalDateTime getDataUltimaTransacao() {
        return dataUltimaTransacao;
    }
    
    public void setDataUltimaTransacao(LocalDateTime dataUltimaTransacao) {
        this.dataUltimaTransacao = dataUltimaTransacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
