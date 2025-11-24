package com.invest.utils;

import com.invest.model.TipoAtivo;
import com.invest.model.TipoTransacao;
import com.invest.model.Transacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilitário para cálculo simplificado de impostos (IR) em operações de renda variável no Brasil.
 * Regras simplificadas aplicadas:
 * - Ações (swing trade): 15% sobre ganho líquido do mês, com isenção se total de vendas do mês < R$ 20.000,00
 * - FIIs/ETFs: 20% sobre ganho líquido do mês, sem isenção do limite de 20k
 * - Day trade: 20% sobre ganho líquido do mês (não detalhamos aqui segregação de day trade)
 * - Compras e proventos não geram imposto
 * Observação: Este utilitário é um aproximador educacional e não substitui a apuração fiscal oficial.
 */
public final class CalculadoraImpostos {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal LIMITE_ISENCAO_ACOES = new BigDecimal("20000.00");
    private static final BigDecimal ALIQUOTA_ACOES = new BigDecimal("0.15");
    private static final BigDecimal ALIQUOTA_FII_ETF = new BigDecimal("0.20");
    private static final int SCALE = 2;

    private CalculadoraImpostos() {
        throw new UnsupportedOperationException("Esta classe não pode ser instanciada");
    }

    /**
     * Calcula o imposto devido para a lista de transações, por mês, retornando o total.
     * Considera apenas transações de VENDA com ganho; compensações de prejuízo não detalhadas.
     */
    public static BigDecimal calcularImpostosAproximados(List<Transacao> transacoes) {
        if (transacoes == null || transacoes.isEmpty()) {
            return ZERO;
        }

        Map<YearMonth, List<Transacao>> vendasPorMes = transacoes.stream()
                .filter(t -> t.getTipoTransacao() == TipoTransacao.VENDA)
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getDataTransacao())));

        BigDecimal totalImposto = ZERO;

        for (Map.Entry<YearMonth, List<Transacao>> entry : vendasPorMes.entrySet()) {
            List<Transacao> vendasMes = entry.getValue();

            BigDecimal totalVendidoMes = vendasMes.stream()
                    .map(Transacao::getValorTotal)
                    .filter(Objects::nonNull)
                    .reduce(ZERO, BigDecimal::add);

            BigDecimal ganhoAcoes = ganhoLiquidoPorTipo(vendasMes, TipoAtivo.ACAO);
            BigDecimal ganhoFii = ganhoLiquidoPorTipo(vendasMes, TipoAtivo.FII);
            BigDecimal ganhoEtf = ganhoLiquidoPorTipo(vendasMes, TipoAtivo.ETF);

            BigDecimal impostoMes = ZERO;

            if (ganhoAcoes.compareTo(ZERO) > 0) {
                if (totalVendidoMes.compareTo(LIMITE_ISENCAO_ACOES) >= 0) {
                    impostoMes = impostoMes.add(ganhoAcoes.multiply(ALIQUOTA_ACOES));
                }
            }

            if (ganhoFii.compareTo(ZERO) > 0) {
                impostoMes = impostoMes.add(ganhoFii.multiply(ALIQUOTA_FII_ETF));
            }

            if (ganhoEtf.compareTo(ZERO) > 0) {
                impostoMes = impostoMes.add(ganhoEtf.multiply(ALIQUOTA_FII_ETF));
            }

            totalImposto = totalImposto.add(impostoMes);
        }

        return totalImposto.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal ganhoLiquidoPorTipo(List<Transacao> vendasMes, TipoAtivo tipoAtivo) {
        BigDecimal ganho = ZERO;
        for (Transacao venda : vendasMes) {
            if (venda.getTipoAtivo() != tipoAtivo) {
                continue;
            }
            BigDecimal receitaVenda = venda.getValorLiquido() != null ? venda.getValorLiquido() : venda.getValorTotal();
            if (receitaVenda == null) {
                continue;
            }

            if (venda.getAtivo() == null || venda.getAtivo().getPrecoCompra() == null) {
                continue;
            }

            BigDecimal custo = venda.getQuantidade().multiply(venda.getAtivo().getPrecoCompra());
            BigDecimal ganhoTransacao = receitaVenda.subtract(custo);
            if (ganhoTransacao.compareTo(ZERO) > 0) {
                ganho = ganho.add(ganhoTransacao);
            }
        }
        return ganho;
    }
}


