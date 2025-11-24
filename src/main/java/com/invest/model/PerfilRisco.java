package com.invest.model;

/**
 * Enum que define os perfis de risco para uma carteira de investimentos
 */
public enum PerfilRisco {
    BAIXO_RISCO("Baixo Risco"),
    MODERADO_RISCO("Moderado Risco"),
    ALTO_RISCO("Alto Risco");
    
    private final String descricao;
    
    PerfilRisco(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    /**
     * Converte uma string para o enum, com tratamento de valores inválidos.
     * Tenta diferentes variações do nome antes de retornar um valor padrão.
     * 
     * @param value O valor a ser convertido
     * @return O enum correspondente ou BAIXO_RISCO como padrão se não encontrar
     */
    public static PerfilRisco fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BAIXO_RISCO;
        }
        
        // Tenta converter diretamente
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Tenta variações comuns
            String normalized = value.toUpperCase().trim()
                .replace(" ", "_")
                .replace("-", "_");
            
            try {
                return valueOf(normalized);
            } catch (IllegalArgumentException e2) {
                // Se ainda não encontrar, tenta mapear valores comuns
                if (normalized.contains("BAIXO") || normalized.contains("LOW") || 
                    normalized.contains("CONSERVADOR") || normalized.contains("CONSERVATIVE")) {
                    return BAIXO_RISCO;
                }
                if (normalized.contains("MODERADO") || normalized.contains("MODERATE") || 
                    normalized.contains("RICO_MODERADO") || normalized.contains("RICO_MODERATE") ||
                    normalized.contains("MODERADO_RISCO")) {
                    return MODERADO_RISCO;
                }
                if (normalized.contains("ALTO") || normalized.contains("HIGH") || 
                    normalized.contains("ARROJADO") || normalized.contains("AGGRESSIVE")) {
                    return ALTO_RISCO;
                }
                
                // Valor padrão se não conseguir mapear
                System.err.println("Valor inválido de PerfilRisco no banco de dados: '" + value + 
                                 "'. Usando BAIXO_RISCO como padrão.");
                return BAIXO_RISCO;
            }
        }
    }
}
