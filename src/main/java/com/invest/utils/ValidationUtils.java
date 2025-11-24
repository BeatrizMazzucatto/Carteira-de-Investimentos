package com.invest.utils;

import com.invest.constants.ApplicationConstants;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utilitário para validações seguindo princípios de Clean Code
 * Responsabilidade única: validações de dados
 */
public final class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    private ValidationUtils() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }
    
    /**
     * Valida se o nome está dentro dos limites permitidos
     * @param nome nome a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidName(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        
        String trimmedName = nome.trim();
        return trimmedName.length() >= ApplicationConstants.NOME_MIN_LENGTH && 
               trimmedName.length() <= ApplicationConstants.NOME_MAX_LENGTH;
    }
    
    /**
     * Valida formato de email
     * @param email email a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Valida se a descrição está dentro do limite permitido
     * @param descricao descrição a ser validada
     * @return true se válida, false caso contrário
     */
    public static boolean isValidDescription(String descricao) {
        if (descricao == null) {
            return true; // Descrição é opcional
        }
        
        return descricao.length() <= ApplicationConstants.DESCRICAO_MAX_LENGTH;
    }
    
    /**
     * Valida se as observações estão dentro do limite permitido
     * @param observacoes observações a serem validadas
     * @return true se válidas, false caso contrário
     */
    public static boolean isValidObservations(String observacoes) {
        if (observacoes == null) {
            return true; // Observações são opcionais
        }
        
        return observacoes.length() <= ApplicationConstants.OBSERVACOES_MAX_LENGTH;
    }
    
    /**
     * Valida código do ativo
     * @param codigoAtivo código a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidAssetCode(String codigoAtivo) {
        if (codigoAtivo == null || codigoAtivo.trim().isEmpty()) {
            return false;
        }
        
        String trimmedCode = codigoAtivo.trim().toUpperCase();
        return trimmedCode.length() <= ApplicationConstants.CODIGO_ATIVO_MAX_LENGTH &&
               trimmedCode.matches("^[A-Z0-9]+$");
    }
    
    /**
     * Valida quantidade de ativo
     * @param quantidade quantidade a ser validada
     * @return true se válida, false caso contrário
     */
    public static boolean isValidAssetQuantity(BigDecimal quantidade) {
        return FinancialCalculator.isValidQuantity(quantidade);
    }
    
    /**
     * Valida preço de ativo
     * @param preco preço a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidAssetPrice(BigDecimal preco) {
        return FinancialCalculator.isValidMonetaryValue(preco);
    }
    
    /**
     * Valida valor monetário (pode ser zero)
     * @param valor valor a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidMonetaryValue(BigDecimal valor) {
        return FinancialCalculator.isValidMonetaryValue(valor);
    }
    
    /**
     * Valida parâmetros de paginação
     * @param page número da página
     * @param size tamanho da página
     * @return true se válidos, false caso contrário
     */
    public static boolean isValidPagination(int page, int size) {
        return page >= ApplicationConstants.DEFAULT_PAGE_NUMBER && 
               size > 0 && 
               size <= ApplicationConstants.MAX_PAGE_SIZE;
    }
    
    /**
     * Valida ID (deve ser positivo)
     * @param id ID a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }
    
    /**
     * Valida string não nula e não vazia
     * @param value string a ser validada
     * @return true se válida, false caso contrário
     */
    public static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Valida objeto não nulo
     * @param obj objeto a ser validado
     * @return true se não nulo, false caso contrário
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }
    
    /**
     * Valida se uma lista não é nula e não está vazia
     * @param list lista a ser validada
     * @return true se válida, false caso contrário
     */
    public static boolean isNotEmpty(java.util.Collection<?> list) {
        return list != null && !list.isEmpty();
    }
    
    /**
     * Valida se um array não é nulo e não está vazio
     * @param array array a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }
    
    /**
     * Valida limite para top/worst performers
     * @param limit limite a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidPerformanceLimit(int limit) {
        return limit > 0 && limit <= 50; // Máximo de 50 para evitar sobrecarga
    }
    
    /**
     * Sanitiza string removendo espaços extras
     * @param input string de entrada
     * @return string sanitizada
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Sanitiza código do ativo (maiúsculo e sem espaços)
     * @param codigo código de entrada
     * @return código sanitizado
     */
    public static String sanitizeAssetCode(String codigo) {
        if (codigo == null) {
            return null;
        }
        
        return codigo.trim().toUpperCase().replaceAll("\\s+", "");
    }
}
