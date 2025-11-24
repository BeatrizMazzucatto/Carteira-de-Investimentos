package com.invest.model;

/**
 * Enum que define os prazos para uma carteira de investimentos
 */
public enum PrazoCarteira {
    CURTO_PRAZO("Curto Prazo"),
    MEDIO_PRAZO("Médio Prazo"),
    LONGO_PRAZO("Longo Prazo");
    
    private final String descricao;
    
    PrazoCarteira(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}

