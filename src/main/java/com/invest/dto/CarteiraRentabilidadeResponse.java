package com.invest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de rentabilidade de carteira completa
 */
public class CarteiraRentabilidadeResponse {
    
    private Long carteiraId;
    private String carteiraNome;
    
    // Resumo Geral
    private BigDecimal valorTotalInvestido;
    private BigDecimal valorAtualMercado;
    private BigDecimal valorAtualComProventos;
    private BigDecimal rentabilidadeBruta;
    private BigDecimal rentabilidadeLiquida;
    private BigDecimal rentabilidadePercentualBruta;
    private BigDecimal rentabilidadePercentualLiquida;
    private BigDecimal rentabilidadePercentualAnual;
    
    // Composição
    private BigDecimal valorTotalCompras;
    private BigDecimal valorTotalVendas;
    private BigDecimal valorTotalProventos;
    private BigDecimal totalTaxasCorretagem;
    private BigDecimal totalImpostos;
    private BigDecimal totalCustos;
    
    // Métricas de Risco
    private BigDecimal volatilidade;
    private BigDecimal sharpeRatio;
    private BigDecimal maxDrawdown;
    private BigDecimal var95;
    
    // Distribuição por Tipo de Ativo
    private BigDecimal percentualAcoes;
    private BigDecimal percentualFIIs;
    private BigDecimal percentualETFs;
    private BigDecimal percentualRendaFixa;
    private BigDecimal percentualCripto;
    
    // Performance por Período
    private BigDecimal rentabilidadeMes;
    private BigDecimal rentabilidadeTrimestre;
    private BigDecimal rentabilidadeSemestre;
    private BigDecimal rentabilidadeAno;
    private BigDecimal rentabilidadeYTD;
    
    // Datas
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
    private LocalDateTime dataPrimeiraCompra;
    private LocalDateTime dataUltimaTransacao;
    
    // Lista de Ativos
    private List<RentabilidadeResponse> ativos;
    private Integer totalAtivos;
    private Integer ativosPositivos;
    private Integer ativosNegativos;
    
    // Construtores
    public CarteiraRentabilidadeResponse() {}
    
    public CarteiraRentabilidadeResponse(Long carteiraId, String carteiraNome) {
        this.carteiraId = carteiraId;
        this.carteiraNome = carteiraNome;
    }
    
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
    
    public BigDecimal getValorTotalInvestido() {
        return valorTotalInvestido;
    }
    
    public void setValorTotalInvestido(BigDecimal valorTotalInvestido) {
        this.valorTotalInvestido = valorTotalInvestido;
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
    
    public BigDecimal getVolatilidade() {
        return volatilidade;
    }
    
    public void setVolatilidade(BigDecimal volatilidade) {
        this.volatilidade = volatilidade;
    }
    
    public BigDecimal getSharpeRatio() {
        return sharpeRatio;
    }
    
    public void setSharpeRatio(BigDecimal sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }
    
    public BigDecimal getMaxDrawdown() {
        return maxDrawdown;
    }
    
    public void setMaxDrawdown(BigDecimal maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }
    
    public BigDecimal getVar95() {
        return var95;
    }
    
    public void setVar95(BigDecimal var95) {
        this.var95 = var95;
    }
    
    public BigDecimal getPercentualAcoes() {
        return percentualAcoes;
    }
    
    public void setPercentualAcoes(BigDecimal percentualAcoes) {
        this.percentualAcoes = percentualAcoes;
    }
    
    public BigDecimal getPercentualFIIs() {
        return percentualFIIs;
    }
    
    public void setPercentualFIIs(BigDecimal percentualFIIs) {
        this.percentualFIIs = percentualFIIs;
    }
    
    public BigDecimal getPercentualETFs() {
        return percentualETFs;
    }
    
    public void setPercentualETFs(BigDecimal percentualETFs) {
        this.percentualETFs = percentualETFs;
    }
    
    public BigDecimal getPercentualRendaFixa() {
        return percentualRendaFixa;
    }
    
    public void setPercentualRendaFixa(BigDecimal percentualRendaFixa) {
        this.percentualRendaFixa = percentualRendaFixa;
    }
    
    public BigDecimal getPercentualCripto() {
        return percentualCripto;
    }
    
    public void setPercentualCripto(BigDecimal percentualCripto) {
        this.percentualCripto = percentualCripto;
    }
    
    public BigDecimal getRentabilidadeMes() {
        return rentabilidadeMes;
    }
    
    public void setRentabilidadeMes(BigDecimal rentabilidadeMes) {
        this.rentabilidadeMes = rentabilidadeMes;
    }
    
    public BigDecimal getRentabilidadeTrimestre() {
        return rentabilidadeTrimestre;
    }
    
    public void setRentabilidadeTrimestre(BigDecimal rentabilidadeTrimestre) {
        this.rentabilidadeTrimestre = rentabilidadeTrimestre;
    }
    
    public BigDecimal getRentabilidadeSemestre() {
        return rentabilidadeSemestre;
    }
    
    public void setRentabilidadeSemestre(BigDecimal rentabilidadeSemestre) {
        this.rentabilidadeSemestre = rentabilidadeSemestre;
    }
    
    public BigDecimal getRentabilidadeAno() {
        return rentabilidadeAno;
    }
    
    public void setRentabilidadeAno(BigDecimal rentabilidadeAno) {
        this.rentabilidadeAno = rentabilidadeAno;
    }
    
    public BigDecimal getRentabilidadeYTD() {
        return rentabilidadeYTD;
    }
    
    public void setRentabilidadeYTD(BigDecimal rentabilidadeYTD) {
        this.rentabilidadeYTD = rentabilidadeYTD;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }
    
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
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
    
    public List<RentabilidadeResponse> getAtivos() {
        return ativos;
    }
    
    public void setAtivos(List<RentabilidadeResponse> ativos) {
        this.ativos = ativos;
    }
    
    public Integer getTotalAtivos() {
        return totalAtivos;
    }
    
    public void setTotalAtivos(Integer totalAtivos) {
        this.totalAtivos = totalAtivos;
    }
    
    public Integer getAtivosPositivos() {
        return ativosPositivos;
    }
    
    public void setAtivosPositivos(Integer ativosPositivos) {
        this.ativosPositivos = ativosPositivos;
    }
    
    public Integer getAtivosNegativos() {
        return ativosNegativos;
    }
    
    public void setAtivosNegativos(Integer ativosNegativos) {
        this.ativosNegativos = ativosNegativos;
    }
}
