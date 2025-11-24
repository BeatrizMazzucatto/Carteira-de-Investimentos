package com.invest.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para cálculos de inflação e valores deflacionados
 * Utiliza IPCA (Índice Nacional de Preços ao Consumidor Amplo) como referência
 */
@Service
public class InflacaoService {

    // Taxa de inflação mensal média aproximada do IPCA (pode ser substituída por dados reais)
    // Valores em percentual mensal (ex: 0.5 = 0,5% ao mês)
    private static final Map<String, BigDecimal> TAXA_INFLACAO_MENSAL = new HashMap<>();
    
    static {
        // Taxas mensais aproximadas do IPCA (exemplo - em produção, buscar de API do IBGE)
        // Formato: "YYYY-MM" -> taxa mensal em decimal
        TAXA_INFLACAO_MENSAL.put("2024-01", new BigDecimal("0.0042")); // 0,42%
        TAXA_INFLACAO_MENSAL.put("2024-02", new BigDecimal("0.0041")); // 0,41%
        TAXA_INFLACAO_MENSAL.put("2024-03", new BigDecimal("0.0016")); // 0,16%
        TAXA_INFLACAO_MENSAL.put("2024-04", new BigDecimal("0.0038")); // 0,38%
        TAXA_INFLACAO_MENSAL.put("2024-05", new BigDecimal("0.0044")); // 0,44%
        TAXA_INFLACAO_MENSAL.put("2024-06", new BigDecimal("0.0021")); // 0,21%
        TAXA_INFLACAO_MENSAL.put("2024-07", new BigDecimal("0.0017")); // 0,17%
        TAXA_INFLACAO_MENSAL.put("2024-08", new BigDecimal("0.0024")); // 0,24%
        TAXA_INFLACAO_MENSAL.put("2024-09", new BigDecimal("0.0026")); // 0,26%
        TAXA_INFLACAO_MENSAL.put("2024-10", new BigDecimal("0.0021")); // 0,21%
        TAXA_INFLACAO_MENSAL.put("2024-11", new BigDecimal("0.0025")); // 0,25%
        TAXA_INFLACAO_MENSAL.put("2024-12", new BigDecimal("0.0030")); // 0,30%
        
        // Adiciona dados para 2025 (atualiza conforme necessário)
        TAXA_INFLACAO_MENSAL.put("2025-01", new BigDecimal("0.0045")); // 0,45%
        TAXA_INFLACAO_MENSAL.put("2025-02", new BigDecimal("0.0040")); // 0,40%
        TAXA_INFLACAO_MENSAL.put("2025-03", new BigDecimal("0.0018")); // 0,18%
        TAXA_INFLACAO_MENSAL.put("2025-04", new BigDecimal("0.0035")); // 0,35%
        TAXA_INFLACAO_MENSAL.put("2025-05", new BigDecimal("0.0042")); // 0,42%
        TAXA_INFLACAO_MENSAL.put("2025-06", new BigDecimal("0.0020")); // 0,20%
        TAXA_INFLACAO_MENSAL.put("2025-07", new BigDecimal("0.0019")); // 0,19%
        TAXA_INFLACAO_MENSAL.put("2025-08", new BigDecimal("0.0025")); // 0,25%
        TAXA_INFLACAO_MENSAL.put("2025-09", new BigDecimal("0.0027")); // 0,27%
        TAXA_INFLACAO_MENSAL.put("2025-10", new BigDecimal("0.0022")); // 0,22%
        TAXA_INFLACAO_MENSAL.put("2025-11", new BigDecimal("0.0026")); // 0,26%
        TAXA_INFLACAO_MENSAL.put("2025-12", new BigDecimal("0.0031")); // 0,31%
        
        // Taxa média histórica do IPCA (usada como fallback): ~0,3% ao mês
        TAXA_INFLACAO_MENSAL.put("MEDIA", new BigDecimal("0.003")); // 0,3% ao mês (ajustado)
    }

    /**
     * Calcula a inflação acumulada entre duas datas
     * @param dataInicial Data inicial
     * @param dataFinal Data final
     * @return Taxa de inflação acumulada (em decimal, ex: 0.10 = 10%)
     */
    public BigDecimal calcularInflacaoAcumulada(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("Data inicial deve ser anterior à data final");
        }

        // Se as datas são iguais, retorna zero
        if (dataInicial.equals(dataFinal)) {
            return BigDecimal.ZERO;
        }

        BigDecimal fatorAcumulado = BigDecimal.ONE;
        
        // Calcula mês a mês, começando do mês inicial até o mês final
        LocalDate dataAtual = dataInicial.withDayOfMonth(1);
        LocalDate dataFinalMes = dataFinal.withDayOfMonth(1);
        
        // Se está no mesmo mês, calcula proporcionalmente
        if (dataAtual.equals(dataFinalMes)) {
            int diasNoMes = dataInicial.lengthOfMonth();
            int diasDecorridos = dataFinal.getDayOfMonth() - dataInicial.getDayOfMonth();
            if (diasDecorridos <= 0) {
                return BigDecimal.ZERO;
            }
            BigDecimal proporcao = new BigDecimal(diasDecorridos)
                .divide(new BigDecimal(diasNoMes), 4, RoundingMode.HALF_UP);
            String chave = dataInicial.getYear() + "-" + String.format("%02d", dataInicial.getMonthValue());
            BigDecimal taxaMensal = TAXA_INFLACAO_MENSAL.getOrDefault(chave, TAXA_INFLACAO_MENSAL.get("MEDIA"));
            BigDecimal taxaProporcional = taxaMensal.multiply(proporcao);
            return taxaProporcional.setScale(4, RoundingMode.HALF_UP);
        }
        
        // Para múltiplos meses: calcula meses completos
        while (!dataAtual.isAfter(dataFinalMes)) {
            String chave = dataAtual.getYear() + "-" + String.format("%02d", dataAtual.getMonthValue());
            BigDecimal taxaMensal = TAXA_INFLACAO_MENSAL.getOrDefault(chave, TAXA_INFLACAO_MENSAL.get("MEDIA"));
            
            // Fator = 1 + taxa
            BigDecimal fator = BigDecimal.ONE.add(taxaMensal);
            fatorAcumulado = fatorAcumulado.multiply(fator);
            
            // Avança para o próximo mês
            dataAtual = dataAtual.plusMonths(1);
        }
        
        // Ajusta proporção do mês inicial (se não começou no dia 1)
        if (dataInicial.getDayOfMonth() > 1) {
            int diasNoMesInicial = dataInicial.lengthOfMonth();
            int diasUsados = diasNoMesInicial - dataInicial.getDayOfMonth() + 1;
            BigDecimal proporcaoInicial = new BigDecimal(diasUsados)
                .divide(new BigDecimal(diasNoMesInicial), 4, RoundingMode.HALF_UP);
            String chaveInicial = dataInicial.getYear() + "-" + String.format("%02d", dataInicial.getMonthValue());
            BigDecimal taxaInicial = TAXA_INFLACAO_MENSAL.getOrDefault(chaveInicial, TAXA_INFLACAO_MENSAL.get("MEDIA"));
            // Remove o fator completo do mês inicial e adiciona o proporcional
            BigDecimal fatorCompletoInicial = BigDecimal.ONE.add(taxaInicial);
            fatorAcumulado = fatorAcumulado.divide(fatorCompletoInicial, 10, RoundingMode.HALF_UP);
            BigDecimal fatorProporcionalInicial = BigDecimal.ONE.add(taxaInicial.multiply(proporcaoInicial));
            fatorAcumulado = fatorAcumulado.multiply(fatorProporcionalInicial);
        }
        
        // Ajusta proporção do mês final (se não terminou no último dia)
        if (dataFinal.getDayOfMonth() < dataFinal.lengthOfMonth()) {
            int diasNoMesFinal = dataFinal.lengthOfMonth();
            int diasUsados = dataFinal.getDayOfMonth();
            BigDecimal proporcaoFinal = new BigDecimal(diasUsados)
                .divide(new BigDecimal(diasNoMesFinal), 4, RoundingMode.HALF_UP);
            String chaveFinal = dataFinal.getYear() + "-" + String.format("%02d", dataFinal.getMonthValue());
            BigDecimal taxaFinal = TAXA_INFLACAO_MENSAL.getOrDefault(chaveFinal, TAXA_INFLACAO_MENSAL.get("MEDIA"));
            // Remove o fator completo do mês final e adiciona o proporcional
            BigDecimal fatorCompletoFinal = BigDecimal.ONE.add(taxaFinal);
            fatorAcumulado = fatorAcumulado.divide(fatorCompletoFinal, 10, RoundingMode.HALF_UP);
            BigDecimal fatorProporcionalFinal = BigDecimal.ONE.add(taxaFinal.multiply(proporcaoFinal));
            fatorAcumulado = fatorAcumulado.multiply(fatorProporcionalFinal);
        }

        // Retorna a inflação acumulada (fator - 1)
        BigDecimal resultado = fatorAcumulado.subtract(BigDecimal.ONE);
        
        // Arredonda para 4 casas decimais
        return resultado.setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Calcula quanto um valor atual correspondia em uma data passada (deflaciona)
     * @param valorAtual Valor atual
     * @param dataAtual Data do valor atual
     * @param dataPassada Data de referência (para a qual queremos deflacionar)
     * @return Valor deflacionado (quanto valia na data passada)
     */
    public BigDecimal calcularValorDeflacionado(BigDecimal valorAtual, LocalDate dataAtual, LocalDate dataPassada) {
        if (dataPassada.isAfter(dataAtual)) {
            throw new IllegalArgumentException("Data passada deve ser anterior à data atual");
        }

        // Se as datas são iguais, retorna o valor atual (sem deflação)
        if (dataPassada.equals(dataAtual)) {
            return valorAtual.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal inflacaoAcumulada = calcularInflacaoAcumulada(dataPassada, dataAtual);
        
        // Se não há inflação, retorna o valor atual
        if (inflacaoAcumulada.compareTo(BigDecimal.ZERO) == 0) {
            return valorAtual.setScale(2, RoundingMode.HALF_UP);
        }
        
        BigDecimal fatorInflacao = BigDecimal.ONE.add(inflacaoAcumulada);
        
        // Valor deflacionado = valor atual / fator de inflação
        // Usa 10 casas decimais para precisão e depois arredonda para 2
        return valorAtual.divide(fatorInflacao, 10, RoundingMode.HALF_UP)
                         .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula quanto um valor passado corresponderia hoje (inflaciona)
     * @param valorPassado Valor na data passada
     * @param dataPassada Data do valor passado
     * @param dataAtual Data atual (ou futura)
     * @return Valor inflacionado (quanto valeria hoje)
     */
    public BigDecimal calcularValorInflacionado(BigDecimal valorPassado, LocalDate dataPassado, LocalDate dataAtual) {
        if (dataPassado.isAfter(dataAtual)) {
            throw new IllegalArgumentException("Data passada deve ser anterior à data atual");
        }

        BigDecimal inflacaoAcumulada = calcularInflacaoAcumulada(dataPassado, dataAtual);
        BigDecimal fatorInflacao = BigDecimal.ONE.add(inflacaoAcumulada);
        
        // Valor inflacionado = valor passado * fator de inflação
        return valorPassado.multiply(fatorInflacao).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o ganho real (ganho nominal descontado da inflação)
     * @param valorInicial Valor inicial investido
     * @param valorFinal Valor final obtido
     * @param dataInicial Data do investimento inicial
     * @param dataFinal Data do valor final
     * @return Ganho real (em decimal, ex: 0.05 = 5% de ganho real)
     */
    public BigDecimal calcularGanhoReal(BigDecimal valorInicial, BigDecimal valorFinal, 
                                       LocalDate dataInicial, LocalDate dataFinal) {
        // Ganho nominal
        BigDecimal ganhoNominal = valorFinal.subtract(valorInicial);
        BigDecimal ganhoNominalPercentual = BigDecimal.ZERO;
        if (valorInicial.compareTo(BigDecimal.ZERO) > 0) {
            ganhoNominalPercentual = ganhoNominal.divide(valorInicial, 4, RoundingMode.HALF_UP);
        }

        // Inflação acumulada
        BigDecimal inflacaoAcumulada = calcularInflacaoAcumulada(dataInicial, dataFinal);

        // Ganho real = (1 + ganho nominal) / (1 + inflação) - 1
        BigDecimal umMaisGanho = BigDecimal.ONE.add(ganhoNominalPercentual);
        BigDecimal umMaisInflacao = BigDecimal.ONE.add(inflacaoAcumulada);
        BigDecimal ganhoReal = umMaisGanho.divide(umMaisInflacao, 4, RoundingMode.HALF_UP)
                                          .subtract(BigDecimal.ONE);

        return ganhoReal;
    }

    /**
     * Calcula o poder de compra de um valor (quanto ele pode comprar hoje vs. no passado)
     * @param valor Valor a analisar
     * @param dataPassada Data de referência passada
     * @param dataAtual Data atual
     * @return Poder de compra (valor deflacionado / valor atual) - indica quantas vezes mais/menos pode comprar
     */
    public BigDecimal calcularPoderDeCompra(BigDecimal valor, LocalDate dataPassada, LocalDate dataAtual) {
        BigDecimal valorDeflacionado = calcularValorDeflacionado(valor, dataAtual, dataPassada);
        
        // Poder de compra = valor deflacionado / valor atual
        // Se > 1, tinha mais poder de compra no passado
        // Se < 1, tem mais poder de compra hoje
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            return valorDeflacionado.divide(valor, 4, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcula a taxa de inflação anualizada aproximada
     * @param dataInicial Data inicial
     * @param dataFinal Data final
     * @return Taxa anualizada (em decimal)
     */
    public BigDecimal calcularTaxaAnualizada(LocalDate dataInicial, LocalDate dataFinal) {
        long meses = ChronoUnit.MONTHS.between(dataInicial, dataFinal);
        if (meses <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal inflacaoAcumulada = calcularInflacaoAcumulada(dataInicial, dataFinal);
        
        // Taxa anualizada aproximada: inflação acumulada * (12 / meses)
        // Esta é uma aproximação linear válida para períodos curtos e taxas pequenas
        BigDecimal taxaAnualizada = inflacaoAcumulada.multiply(new BigDecimal("12"))
                                                     .divide(new BigDecimal(meses), 4, RoundingMode.HALF_UP);
        
        return taxaAnualizada;
    }
}

