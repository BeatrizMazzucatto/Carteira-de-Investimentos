package com.invest.utils;

import com.invest.constants.ApplicationConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Utilitário para cálculos financeiros seguindo princípios de Clean Code
 * Responsabilidade única: cálculos matemáticos financeiros
 */
public final class FinancialCalculator {
    
    private FinancialCalculator() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }
    
    /**
     * Calcula preço médio ponderado
     * @param quantidadeAtual quantidade atual do ativo
     * @param precoMedioAtual preço médio atual
     * @param quantidadeNova nova quantidade comprada
     * @param precoNovaCompra preço da nova compra
     * @return preço médio ponderado
     */
    public static BigDecimal calcularPrecoMedioPonderado(BigDecimal quantidadeAtual, 
                                                         BigDecimal precoMedioAtual,
                                                         BigDecimal quantidadeNova, 
                                                         BigDecimal precoNovaCompra) {
        if (quantidadeAtual == null || precoMedioAtual == null || 
            quantidadeNova == null || precoNovaCompra == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal valorAtual = quantidadeAtual.multiply(precoMedioAtual);
        BigDecimal valorNovaCompra = quantidadeNova.multiply(precoNovaCompra);
        BigDecimal quantidadeTotal = quantidadeAtual.add(quantidadeNova);
        
        if (quantidadeTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return valorAtual.add(valorNovaCompra)
                .divide(quantidadeTotal, ApplicationConstants.PRECISION_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula rentabilidade percentual
     * @param valorAtual valor atual do investimento
     * @param valorInvestido valor total investido
     * @return rentabilidade percentual
     */
    public static BigDecimal calcularRentabilidadePercentual(BigDecimal valorAtual, BigDecimal valorInvestido) {
        if (valorAtual == null || valorInvestido == null || valorInvestido.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return valorAtual.subtract(valorInvestido)
                .divide(valorInvestido, ApplicationConstants.PRECISION_SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(ApplicationConstants.PERCENTAGE_MULTIPLIER));
    }
    
    /**
     * Calcula rentabilidade anualizada
     * @param rentabilidadePercentual rentabilidade percentual total
     * @param dataInicio data de início do investimento
     * @return rentabilidade anualizada
     */
    public static BigDecimal calcularRentabilidadeAnualizada(BigDecimal rentabilidadePercentual, LocalDateTime dataInicio) {
        if (rentabilidadePercentual == null || dataInicio == null) {
            return BigDecimal.ZERO;
        }
        
        long diasInvestimento = ChronoUnit.DAYS.between(dataInicio, LocalDateTime.now());
        if (diasInvestimento <= 0) {
            return BigDecimal.ZERO;
        }
        
        return rentabilidadePercentual
                .multiply(BigDecimal.valueOf(ApplicationConstants.ANNUAL_DAYS))
                .divide(BigDecimal.valueOf(diasInvestimento), ApplicationConstants.PRECISION_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula dividend yield
     * @param proventosTotal total de proventos recebidos
     * @param valorAtualMercado valor atual de mercado
     * @return dividend yield em percentual
     */
    public static BigDecimal calcularDividendYield(BigDecimal proventosTotal, BigDecimal valorAtualMercado) {
        if (proventosTotal == null || valorAtualMercado == null || 
            valorAtualMercado.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return proventosTotal
                .divide(valorAtualMercado, ApplicationConstants.PRECISION_SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(ApplicationConstants.PERCENTAGE_MULTIPLIER));
    }
    
    /**
     * Calcula variação percentual entre dois valores
     * @param valorAtual valor atual
     * @param valorAnterior valor anterior
     * @return variação percentual
     */
    public static BigDecimal calcularVariacaoPercentual(BigDecimal valorAtual, BigDecimal valorAnterior) {
        if (valorAtual == null || valorAnterior == null || valorAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return valorAtual.subtract(valorAnterior)
                .divide(valorAnterior, ApplicationConstants.PRECISION_SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(ApplicationConstants.PERCENTAGE_MULTIPLIER));
    }
    
    /**
     * Calcula valor líquido considerando custos
     * @param valorBruto valor bruto
     * @param taxas taxas aplicadas
     * @param impostos impostos aplicados
     * @return valor líquido
     */
    public static BigDecimal calcularValorLiquido(BigDecimal valorBruto, BigDecimal taxas, BigDecimal impostos) {
        if (valorBruto == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal valorLiquido = valorBruto;
        
        if (taxas != null) {
            valorLiquido = valorLiquido.subtract(taxas);
        }
        
        if (impostos != null) {
            valorLiquido = valorLiquido.subtract(impostos);
        }
        
        return valorLiquido;
    }
    
    /**
     * Calcula preço teto (10% acima do preço atual)
     * @param precoAtual preço atual
     * @return preço teto
     */
    public static BigDecimal calcularPrecoTeto(BigDecimal precoAtual) {
        if (precoAtual == null) {
            return BigDecimal.ZERO;
        }
        
        return precoAtual.multiply(BigDecimal.valueOf(ApplicationConstants.PRECO_TETO_MULTIPLIER))
                .setScale(ApplicationConstants.MONETARY_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula preço suporte (10% abaixo do preço atual)
     * @param precoAtual preço atual
     * @return preço suporte
     */
    public static BigDecimal calcularPrecoSuporte(BigDecimal precoAtual) {
        if (precoAtual == null) {
            return BigDecimal.ZERO;
        }
        
        return precoAtual.multiply(BigDecimal.valueOf(ApplicationConstants.PRECO_SUPORTE_MULTIPLIER))
                .setScale(ApplicationConstants.MONETARY_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula volatilidade simples baseada em variações percentuais
     * @param variacoes lista de variações percentuais
     * @return volatilidade
     */
    public static BigDecimal calcularVolatilidade(java.util.List<BigDecimal> variacoes) {
        if (variacoes == null || variacoes.size() < 2) {
            return BigDecimal.ZERO;
        }
        
        // Calcula média
        BigDecimal soma = variacoes.stream()
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal media = soma.divide(BigDecimal.valueOf(variacoes.size()), 
                ApplicationConstants.PRECISION_SCALE, RoundingMode.HALF_UP);
        
        // Calcula variância
        BigDecimal somaQuadrados = variacoes.stream()
                .filter(v -> v != null)
                .map(v -> v.subtract(media).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal variancia = somaQuadrados.divide(BigDecimal.valueOf(variacoes.size() - 1), 
                ApplicationConstants.PRECISION_SCALE, RoundingMode.HALF_UP);
        
        // Calcula desvio padrão (volatilidade)
        return new BigDecimal(Math.sqrt(variancia.doubleValue()))
                .setScale(ApplicationConstants.PRECISION_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * Valida se um valor monetário é válido
     * @param valor valor a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValidMonetaryValue(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    /**
     * Valida se uma quantidade é válida
     * @param quantidade quantidade a ser validada
     * @return true se válida, false caso contrário
     */
    public static boolean isValidQuantity(BigDecimal quantidade) {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }
}
