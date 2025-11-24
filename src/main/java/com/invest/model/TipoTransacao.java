package com.invest.model;

/**
 * Enum que define os tipos de transações financeiras
 */
public enum TipoTransacao {
    COMPRA("Compra"),
    VENDA("Venda"),
    PROVENTO("Provento"),
    DIVIDENDO("Dividendo"),
    JCP("Juros sobre Capital Próprio"),
    RENDIMENTO("Rendimento"),
    AMORTIZACAO("Amortização"),
    BONIFICACAO("Bonificação"),
    GRUPAMENTO("Grupamento"),
    DESDOBRAMENTO("Desdobramento"),
    SUBSCRICAO("Subscrição"),
    TRANSFERENCIA("Transferência"),
    OUTROS("Outros");
    
    private final String descricao;
    
    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    /**
     * Verifica se é uma transação de entrada (aumenta posição)
     */
    public boolean isEntrada() {
        return this == COMPRA || this == PROVENTO || this == DIVIDENDO || 
               this == JCP || this == RENDIMENTO || this == BONIFICACAO || 
               this == DESDOBRAMENTO || this == SUBSCRICAO;
    }
    
    /**
     * Verifica se é uma transação de saída (diminui posição)
     */
    public boolean isSaida() {
        return this == VENDA || this == AMORTIZACAO || this == GRUPAMENTO;
    }
    
    /**
     * Verifica se é uma transação de provento (não altera quantidade)
     */
    public boolean isProvento() {
        return this == PROVENTO || this == DIVIDENDO || this == JCP || this == RENDIMENTO;
    }
}
