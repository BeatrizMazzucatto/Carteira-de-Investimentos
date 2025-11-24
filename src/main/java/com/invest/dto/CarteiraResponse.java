package com.invest.dto;

import com.invest.model.ObjetivoCarteira;
import com.invest.model.PerfilRisco;
import com.invest.model.PrazoCarteira;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respostas de carteiras
 */
public class CarteiraResponse {
    
    private Long id;
    private String nome;
    private String descricao;
    private ObjetivoCarteira objetivo;
    private PrazoCarteira prazo;
    private PerfilRisco perfilRisco;
    private BigDecimal valorInicial;
    private BigDecimal valorAtual;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String googleSheetsId;
    private Long investidorId;
    private String investidorNome;
    private List<AtivoResponse> ativos;
    private Integer totalAtivos;
    private BigDecimal variacaoPercentual;
    
    // Construtores
    public CarteiraResponse() {}
    
    public CarteiraResponse(Long id, String nome, String descricao, ObjetivoCarteira objetivo, 
                           PerfilRisco perfilRisco, BigDecimal valorInicial, BigDecimal valorAtual,
                           LocalDateTime dataCriacao, LocalDateTime dataAtualizacao, String googleSheetsId,
                           Long investidorId, String investidorNome) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.objetivo = objetivo;
        this.perfilRisco = perfilRisco;
        this.valorInicial = valorInicial;
        this.valorAtual = valorAtual;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.googleSheetsId = googleSheetsId;
        this.investidorId = investidorId;
        this.investidorNome = investidorNome;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public ObjetivoCarteira getObjetivo() {
        return objetivo;
    }
    
    public void setObjetivo(ObjetivoCarteira objetivo) {
        this.objetivo = objetivo;
    }
    
    public PrazoCarteira getPrazo() {
        return prazo;
    }
    
    public void setPrazo(PrazoCarteira prazo) {
        this.prazo = prazo;
    }
    
    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }
    
    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }
    
    public BigDecimal getValorInicial() {
        return valorInicial;
    }
    
    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }
    
    public BigDecimal getValorAtual() {
        return valorAtual;
    }
    
    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
    public String getGoogleSheetsId() {
        return googleSheetsId;
    }
    
    public void setGoogleSheetsId(String googleSheetsId) {
        this.googleSheetsId = googleSheetsId;
    }
    
    public Long getInvestidorId() {
        return investidorId;
    }
    
    public void setInvestidorId(Long investidorId) {
        this.investidorId = investidorId;
    }
    
    public String getInvestidorNome() {
        return investidorNome;
    }
    
    public void setInvestidorNome(String investidorNome) {
        this.investidorNome = investidorNome;
    }
    
    public List<AtivoResponse> getAtivos() {
        return ativos;
    }
    
    public void setAtivos(List<AtivoResponse> ativos) {
        this.ativos = ativos;
    }
    
    public Integer getTotalAtivos() {
        return totalAtivos;
    }
    
    public void setTotalAtivos(Integer totalAtivos) {
        this.totalAtivos = totalAtivos;
    }
    
    public BigDecimal getVariacaoPercentual() {
        return variacaoPercentual;
    }
    
    public void setVariacaoPercentual(BigDecimal variacaoPercentual) {
        this.variacaoPercentual = variacaoPercentual;
    }
}
