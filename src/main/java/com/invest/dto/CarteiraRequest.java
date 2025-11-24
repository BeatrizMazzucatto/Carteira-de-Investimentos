package com.invest.dto;

import com.invest.model.ObjetivoCarteira;
import com.invest.model.PerfilRisco;
import com.invest.model.PrazoCarteira;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para requisições de criação e atualização de carteiras
 */
public class CarteiraRequest {
    
    @NotBlank(message = "Nome da carteira é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    @NotNull(message = "Objetivo da carteira é obrigatório")
    private ObjetivoCarteira objetivo;
    
    private PrazoCarteira prazo;
    
    @NotNull(message = "Perfil de risco é obrigatório")
    private PerfilRisco perfilRisco;
    
    private BigDecimal valorInicial;
    
    private String googleSheetsId;
    
    // Construtores
    public CarteiraRequest() {}
    
    public CarteiraRequest(String nome, String descricao, ObjetivoCarteira objetivo, PerfilRisco perfilRisco) {
        this.nome = nome;
        this.descricao = descricao;
        this.objetivo = objetivo;
        this.perfilRisco = perfilRisco;
    }
    
    // Getters e Setters
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
    
    public String getGoogleSheetsId() {
        return googleSheetsId;
    }
    
    public void setGoogleSheetsId(String googleSheetsId) {
        this.googleSheetsId = googleSheetsId;
    }
}
