package com.invest.model;

/**
 * Enum que define os objetivos possíveis para uma carteira de investimentos
 */
public enum ObjetivoCarteira {
    APOSENTADORIA("Aposentadoria"),
    RESERVA_EMERGENCIAL("Reserva de Emergência"),
    VALORIZACAO_RAPIDA("Valorização Rápida"),
    RENDA_PASSIVA("Renda Passiva"),
    EDUCACAO("Educação"),
    CASA_PROPIA("Casa Própria"),
    VIAGEM("Viagem"),
    OUTROS("Outros");
    
    private final String descricao;
    
    ObjetivoCarteira(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
