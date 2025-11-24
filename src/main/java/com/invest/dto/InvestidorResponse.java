package com.invest.dto;

/**
 * DTO para response de investidores
 * 
 * Classe de transferência de dados para responses da API
 * adaptação do contacts
 */

import java.time.LocalDateTime;

public class InvestidorResponse {
    private Long id;
    private String nome;
    private String email;
    private LocalDateTime dataCriacao;

    public InvestidorResponse() {}

    public InvestidorResponse(Long id, String nome, String email, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataCriacao = dataCriacao;
    }

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
