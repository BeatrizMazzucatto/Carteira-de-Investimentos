package com.invest.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter para PerfilRisco que trata valores inválidos do banco de dados.
 * Converte automaticamente valores inválidos para BAIXO_RISCO como padrão.
 */
@Converter(autoApply = false)
public class PerfilRiscoConverter implements AttributeConverter<PerfilRisco, String> {

    @Override
    public String convertToDatabaseColumn(PerfilRisco perfilRisco) {
        if (perfilRisco == null) {
            return null;
        }
        return perfilRisco.name();
    }

    @Override
    public PerfilRisco convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return PerfilRisco.BAIXO_RISCO; // Valor padrão
        }
        return PerfilRisco.fromString(dbData);
    }
}
