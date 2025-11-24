package com.invest.model;

/**
 * Enum que define os tipos de ativos financeiros
 */
public enum TipoAtivo {
    ACAO("Ação"),
    FII("Fundo Imobiliário"),
    ETF("ETF"),
    BDR("BDR"),
    REIT("REIT"),
    CDB("CDB"),
    LCI("LCI"),
    LCA("LCA"),
    DEBENTURE("Debênture"),
    TESOURO("Tesouro Direto"),
    CRIPTOMOEDA("Criptomoeda"),
    OUTROS("Outros");
    
    private final String descricao;
    
    TipoAtivo(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
