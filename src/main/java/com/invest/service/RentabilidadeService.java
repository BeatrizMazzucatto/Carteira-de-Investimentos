package com.invest.service;

import com.invest.dto.CarteiraRentabilidadeResponse;
import com.invest.dto.RentabilidadeResponse;
import com.invest.model.*;
import com.invest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import com.invest.utils.CalculadoraImpostos;

/**
 * Service para cálculos de rentabilidade de ativos e carteiras
 */
@Service
@Transactional
public class RentabilidadeService {

    @Autowired
    private AtivoRepository ativoRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    /**
     * Calcula rentabilidade de um ativo específico
     */
    public RentabilidadeResponse calcularRentabilidadeAtivo(Long ativoId) {
        Ativo ativo = ativoRepository.findById(ativoId)
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado: " + ativoId));

        Carteira carteira = ativo.getCarteira();
        List<Transacao> transacoes = transacaoRepository.findByCarteiraAndCodigoAtivo(carteira, ativo.getCodigo());

        RentabilidadeResponse response = new RentabilidadeResponse(
                ativo.getId(), ativo.getCodigo(), ativo.getNome()
        );

        // Dados básicos
        response.setQuantidadeAtual(ativo.getQuantidade());
        response.setPrecoMedioCompra(ativo.getPrecoCompra());
        response.setPrecoAtual(ativo.getPrecoAtual());

        // Cálculos de valores
        calcularValoresInvestimento(response, transacoes);
        calcularRentabilidade(response);
        calcularMetricasAdicionais(response, transacoes);
        calcularDatas(response, transacoes);

        return response;
    }

    /**
     * Calcula rentabilidade completa de uma carteira
     */
    public CarteiraRentabilidadeResponse calcularRentabilidadeCarteira(Long carteiraId) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        CarteiraRentabilidadeResponse response = new CarteiraRentabilidadeResponse(
                carteira.getId(), carteira.getNome()
        );

        // Dados básicos da carteira
        response.setDataCriacao(carteira.getDataCriacao());
        response.setDataUltimaAtualizacao(carteira.getDataAtualizacao());

        // Calcula rentabilidade de cada ativo
        // Busca ativos diretamente do repository para evitar LazyInitializationException
        List<Ativo> ativos = ativoRepository.findByCarteira(carteira);
        
        List<RentabilidadeResponse> ativosRentabilidade = new ArrayList<>();
        BigDecimal valorTotalInvestido = BigDecimal.ZERO;
        BigDecimal valorAtualMercado = BigDecimal.ZERO;
        BigDecimal valorAtualComProventos = BigDecimal.ZERO;
        BigDecimal totalCompras = BigDecimal.ZERO;
        BigDecimal totalVendas = BigDecimal.ZERO;
        BigDecimal totalProventos = BigDecimal.ZERO;
        BigDecimal totalTaxas = BigDecimal.ZERO;
        BigDecimal totalImpostos = BigDecimal.ZERO;

        for (Ativo ativo : ativos) {
            RentabilidadeResponse ativoRentabilidade = calcularRentabilidadeAtivo(ativo.getId());
            ativosRentabilidade.add(ativoRentabilidade);

            // Acumula valores (com verificação de null)
            BigDecimal valorInvestido = ativoRentabilidade.getValorTotalInvestido() != null ? ativoRentabilidade.getValorTotalInvestido() : BigDecimal.ZERO;
            BigDecimal valorMercado = ativoRentabilidade.getValorAtualMercado() != null ? ativoRentabilidade.getValorAtualMercado() : BigDecimal.ZERO;
            BigDecimal valorComProventos = ativoRentabilidade.getValorAtualComProventos() != null ? ativoRentabilidade.getValorAtualComProventos() : BigDecimal.ZERO;
            BigDecimal compras = ativoRentabilidade.getValorTotalCompras() != null ? ativoRentabilidade.getValorTotalCompras() : BigDecimal.ZERO;
            BigDecimal vendas = ativoRentabilidade.getValorTotalVendas() != null ? ativoRentabilidade.getValorTotalVendas() : BigDecimal.ZERO;
            BigDecimal proventos = ativoRentabilidade.getValorTotalProventos() != null ? ativoRentabilidade.getValorTotalProventos() : BigDecimal.ZERO;
            BigDecimal taxas = ativoRentabilidade.getTotalTaxasCorretagem() != null ? ativoRentabilidade.getTotalTaxasCorretagem() : BigDecimal.ZERO;
            BigDecimal impostos = ativoRentabilidade.getTotalImpostos() != null ? ativoRentabilidade.getTotalImpostos() : BigDecimal.ZERO;
            
            valorTotalInvestido = valorTotalInvestido.add(valorInvestido);
            valorAtualMercado = valorAtualMercado.add(valorMercado);
            valorAtualComProventos = valorAtualComProventos.add(valorComProventos);
            totalCompras = totalCompras.add(compras);
            totalVendas = totalVendas.add(vendas);
            totalProventos = totalProventos.add(proventos);
            totalTaxas = totalTaxas.add(taxas);
            totalImpostos = totalImpostos.add(impostos);
        }

        // Define valores da carteira
        response.setValorTotalInvestido(valorTotalInvestido != null ? valorTotalInvestido : BigDecimal.ZERO);
        response.setValorAtualMercado(valorAtualMercado != null ? valorAtualMercado : BigDecimal.ZERO);
        response.setValorAtualComProventos(valorAtualComProventos != null ? valorAtualComProventos : BigDecimal.ZERO);
        response.setValorTotalCompras(totalCompras);
        response.setValorTotalVendas(totalVendas);
        response.setValorTotalProventos(totalProventos);
        response.setTotalTaxasCorretagem(totalTaxas);
        response.setTotalImpostos(totalImpostos);
        response.setTotalCustos(totalTaxas.add(totalImpostos));

        // Calcula rentabilidade da carteira
        calcularRentabilidadeCarteira(response);

        // Calcula distribuição por tipo de ativo
        calcularDistribuicaoPorTipo(response, ativos);

        // Calcula métricas de risco
        calcularMetricasRisco(response, ativosRentabilidade);

        // Calcula performance por período
        calcularPerformancePorPeriodo(response, carteira);

        // Define lista de ativos
        response.setAtivos(ativosRentabilidade);
        response.setTotalAtivos(ativosRentabilidade.size());
        response.setAtivosPositivos((int) ativosRentabilidade.stream()
                .filter(a -> a.getRentabilidadeLiquida().compareTo(BigDecimal.ZERO) > 0)
                .count());
        response.setAtivosNegativos((int) ativosRentabilidade.stream()
                .filter(a -> a.getRentabilidadeLiquida().compareTo(BigDecimal.ZERO) < 0)
                .count());

        return response;
    }

    /**
     * Calcula valores de investimento
     */
    private void calcularValoresInvestimento(RentabilidadeResponse response, List<Transacao> transacoes) {
        BigDecimal totalCompras = BigDecimal.ZERO;
        BigDecimal totalVendas = BigDecimal.ZERO;
        BigDecimal totalProventos = BigDecimal.ZERO;
        BigDecimal totalTaxas = BigDecimal.ZERO;
        BigDecimal totalImpostos = BigDecimal.ZERO;

        for (Transacao transacao : transacoes) {
            if (transacao.getTipoTransacao() == TipoTransacao.COMPRA) {
                totalCompras = totalCompras.add(transacao.getValorTotal());
            } else if (transacao.getTipoTransacao() == TipoTransacao.VENDA) {
                totalVendas = totalVendas.add(transacao.getValorTotal());
            } else if (transacao.getTipoTransacao().isProvento()) {
                totalProventos = totalProventos.add(transacao.getValorTotal());
            }

            if (transacao.getTaxasCorretagem() != null) {
                totalTaxas = totalTaxas.add(transacao.getTaxasCorretagem());
            }
            if (transacao.getImpostos() != null) {
                totalImpostos = totalImpostos.add(transacao.getImpostos());
            }
        }

        // Impostos aproximados (quando não informados nas transações ou para completar análise)
        BigDecimal impostosAproximados = CalculadoraImpostos.calcularImpostosAproximados(transacoes);
        if (impostosAproximados.compareTo(totalImpostos) > 0) {
            totalImpostos = impostosAproximados;
        }

        response.setValorTotalCompras(totalCompras);
        response.setValorTotalVendas(totalVendas);
        response.setValorTotalProventos(totalProventos);
        response.setTotalTaxasCorretagem(totalTaxas);
        response.setTotalImpostos(totalImpostos);
        response.setTotalCustos(totalTaxas.add(totalImpostos));

        // Valor total investido (compras - vendas)
        response.setValorTotalInvestido(totalCompras.subtract(totalVendas));
    }

    /**
     * Calcula rentabilidade
     */
    private void calcularRentabilidade(RentabilidadeResponse response) {
        BigDecimal valorInvestido = response.getValorTotalInvestido() != null ? response.getValorTotalInvestido() : BigDecimal.ZERO;
        BigDecimal quantidade = response.getQuantidadeAtual() != null ? response.getQuantidadeAtual() : BigDecimal.ZERO;
        BigDecimal precoAtual = response.getPrecoAtual();
        BigDecimal valorTotalProventos = response.getValorTotalProventos() != null ? response.getValorTotalProventos() : BigDecimal.ZERO;

        if (quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0 && precoAtual != null) {
            // Valor atual de mercado
            BigDecimal valorMercado = quantidade.multiply(precoAtual);
            response.setValorAtualMercado(valorMercado);

            // Valor atual com proventos
            BigDecimal valorComProventos = valorMercado.add(valorTotalProventos);
            response.setValorAtualComProventos(valorComProventos);

            // Rentabilidade bruta (sem custos)
            BigDecimal rentabilidadeBruta = valorMercado.subtract(valorInvestido);
            response.setRentabilidadeBruta(rentabilidadeBruta);

            // Rentabilidade líquida (com custos)
            BigDecimal totalCustos = response.getTotalCustos() != null ? response.getTotalCustos() : BigDecimal.ZERO;
            BigDecimal rentabilidadeLiquida = valorComProventos.subtract(valorInvestido).subtract(totalCustos);
            response.setRentabilidadeLiquida(rentabilidadeLiquida);

            // Rentabilidade percentual bruta
            if (valorInvestido != null && valorInvestido.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rentabilidadePercentualBruta = rentabilidadeBruta
                        .divide(valorInvestido, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                response.setRentabilidadePercentualBruta(rentabilidadePercentualBruta);

                // Rentabilidade percentual líquida
                BigDecimal rentabilidadePercentualLiquida = rentabilidadeLiquida
                        .divide(valorInvestido, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                response.setRentabilidadePercentualLiquida(rentabilidadePercentualLiquida);
            } else {
                response.setRentabilidadePercentualBruta(BigDecimal.ZERO);
                response.setRentabilidadePercentualLiquida(BigDecimal.ZERO);
            }

            // Variação percentual (preço atual vs preço médio)
            BigDecimal precoMedioCompra = response.getPrecoMedioCompra();
            if (precoMedioCompra != null && precoMedioCompra.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal variacaoPercentual = precoAtual
                        .subtract(precoMedioCompra)
                        .divide(precoMedioCompra, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                response.setVariacaoPercentual(variacaoPercentual);
            } else {
                response.setVariacaoPercentual(BigDecimal.ZERO);
            }
        } else {
            // Se não houver quantidade ou preço atual, define valores como zero
            response.setValorAtualMercado(BigDecimal.ZERO);
            response.setValorAtualComProventos(valorTotalProventos);
            BigDecimal totalCustos = response.getTotalCustos() != null ? response.getTotalCustos() : BigDecimal.ZERO;
            response.setRentabilidadeBruta(BigDecimal.ZERO.subtract(valorInvestido));
            response.setRentabilidadeLiquida(valorTotalProventos.subtract(valorInvestido).subtract(totalCustos));
            response.setRentabilidadePercentualBruta(BigDecimal.ZERO);
            response.setRentabilidadePercentualLiquida(BigDecimal.ZERO);
            response.setVariacaoPercentual(BigDecimal.ZERO);
        }
        
        // Variação em valor (só se houver preço atual e quantidade)
        BigDecimal precoAtualFinal = response.getPrecoAtual();
        BigDecimal quantidadeFinal = response.getQuantidadeAtual();
        BigDecimal precoMedioFinal = response.getPrecoMedioCompra();
        if (precoAtualFinal != null && quantidadeFinal != null && precoMedioFinal != null) {
            BigDecimal variacaoValor = precoAtualFinal.subtract(precoMedioFinal).multiply(quantidadeFinal);
            response.setVariacaoValor(variacaoValor);
        } else {
            response.setVariacaoValor(BigDecimal.ZERO);
        }
    }

    /**
     * Calcula métricas adicionais
     */
    private void calcularMetricasAdicionais(RentabilidadeResponse response, List<Transacao> transacoes) {
        // Dividend Yield
        BigDecimal valorAtualMercado = response.getValorAtualMercado();
        BigDecimal valorTotalProventos = response.getValorTotalProventos();
        if (valorAtualMercado != null && valorAtualMercado.compareTo(BigDecimal.ZERO) > 0 && 
            valorTotalProventos != null && valorTotalProventos.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal dividendYield = valorTotalProventos
                    .divide(valorAtualMercado, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setDividendYield(dividendYield);
        } else {
            response.setDividendYield(BigDecimal.ZERO);
        }

        // Preço teto e suporte (simplificado)
        if (response.getPrecoAtual() != null) {
            response.setPrecoTeto(response.getPrecoAtual().multiply(BigDecimal.valueOf(1.1))); // +10%
            response.setPrecoSuporte(response.getPrecoAtual().multiply(BigDecimal.valueOf(0.9))); // -10%
        }
    }

    /**
     * Calcula datas importantes
     */
    private void calcularDatas(RentabilidadeResponse response, List<Transacao> transacoes) {
        if (!transacoes.isEmpty()) {
            // Data da primeira compra
            LocalDateTime primeiraCompra = transacoes.stream()
                    .filter(t -> t.getTipoTransacao() == TipoTransacao.COMPRA)
                    .map(Transacao::getDataTransacao)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            response.setDataPrimeiraCompra(primeiraCompra);

            // Data da última transação
            LocalDateTime ultimaTransacao = transacoes.stream()
                    .map(Transacao::getDataTransacao)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            response.setDataUltimaTransacao(ultimaTransacao);

            // Rentabilidade anual
            if (primeiraCompra != null) {
                long diasInvestimento = ChronoUnit.DAYS.between(primeiraCompra, LocalDateTime.now());
                if (diasInvestimento > 0 && response.getRentabilidadePercentualLiquida() != null) {
                    BigDecimal rentabilidadeAnual = response.getRentabilidadePercentualLiquida()
                            .multiply(BigDecimal.valueOf(365))
                            .divide(BigDecimal.valueOf(diasInvestimento), 4, RoundingMode.HALF_UP);
                    response.setRentabilidadePercentualAnual(rentabilidadeAnual);
                }
            }
        }

        response.setDataAtualizacao(LocalDateTime.now());
    }

    /**
     * Calcula rentabilidade da carteira
     */
    private void calcularRentabilidadeCarteira(CarteiraRentabilidadeResponse response) {
        BigDecimal valorInvestido = response.getValorTotalInvestido() != null ? response.getValorTotalInvestido() : BigDecimal.ZERO;
        BigDecimal valorAtual = response.getValorAtualMercado() != null ? response.getValorAtualMercado() : BigDecimal.ZERO;
        BigDecimal valorComProventos = response.getValorAtualComProventos() != null ? response.getValorAtualComProventos() : BigDecimal.ZERO;
        BigDecimal totalCustos = response.getTotalCustos() != null ? response.getTotalCustos() : BigDecimal.ZERO;

        // Rentabilidade bruta
        BigDecimal rentabilidadeBruta = valorAtual.subtract(valorInvestido);
        response.setRentabilidadeBruta(rentabilidadeBruta);

        // Rentabilidade líquida
        BigDecimal rentabilidadeLiquida = valorComProventos.subtract(valorInvestido).subtract(totalCustos);
        response.setRentabilidadeLiquida(rentabilidadeLiquida);

        // Rentabilidade percentual
        if (valorInvestido != null && valorInvestido.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rentabilidadePercentualBruta = rentabilidadeBruta
                    .divide(valorInvestido, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setRentabilidadePercentualBruta(rentabilidadePercentualBruta);

            BigDecimal rentabilidadePercentualLiquida = rentabilidadeLiquida
                    .divide(valorInvestido, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setRentabilidadePercentualLiquida(rentabilidadePercentualLiquida);
        } else {
            response.setRentabilidadePercentualBruta(BigDecimal.ZERO);
            response.setRentabilidadePercentualLiquida(BigDecimal.ZERO);
        }
    }

    /**
     * Calcula distribuição por tipo de ativo
     */
    private void calcularDistribuicaoPorTipo(CarteiraRentabilidadeResponse response, List<Ativo> ativos) {
        BigDecimal valorTotal = response.getValorAtualMercado() != null ? response.getValorAtualMercado() : BigDecimal.ZERO;
        
        if (valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0) {
            // Ações
            BigDecimal valorAcoes = ativos.stream()
                    .filter(a -> a.getTipo() == TipoAtivo.ACAO)
                    .map(a -> a.getQuantidade().multiply(a.getPrecoAtual() != null ? a.getPrecoAtual() : a.getPrecoCompra()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.setPercentualAcoes(valorAcoes.divide(valorTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));

            // FIIs
            BigDecimal valorFIIs = ativos.stream()
                    .filter(a -> a.getTipo() == TipoAtivo.FII)
                    .map(a -> a.getQuantidade().multiply(a.getPrecoAtual() != null ? a.getPrecoAtual() : a.getPrecoCompra()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.setPercentualFIIs(valorFIIs.divide(valorTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));

            // ETFs
            BigDecimal valorETFs = ativos.stream()
                    .filter(a -> a.getTipo() == TipoAtivo.ETF)
                    .map(a -> a.getQuantidade().multiply(a.getPrecoAtual() != null ? a.getPrecoAtual() : a.getPrecoCompra()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.setPercentualETFs(valorETFs.divide(valorTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));

            // Renda Fixa
            BigDecimal valorRendaFixa = ativos.stream()
                    .filter(a -> a.getTipo() == TipoAtivo.CDB || a.getTipo() == TipoAtivo.LCI || 
                               a.getTipo() == TipoAtivo.LCA || a.getTipo() == TipoAtivo.TESOURO)
                    .map(a -> a.getQuantidade().multiply(a.getPrecoAtual() != null ? a.getPrecoAtual() : a.getPrecoCompra()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.setPercentualRendaFixa(valorRendaFixa.divide(valorTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));

            // Cripto
            BigDecimal valorCripto = ativos.stream()
                    .filter(a -> a.getTipo() == TipoAtivo.CRIPTOMOEDA)
                    .map(a -> a.getQuantidade().multiply(a.getPrecoAtual() != null ? a.getPrecoAtual() : a.getPrecoCompra()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.setPercentualCripto(valorCripto.divide(valorTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        }
    }

    /**
     * Calcula métricas de risco (simplificado)
     */
    private void calcularMetricasRisco(CarteiraRentabilidadeResponse response, List<RentabilidadeResponse> ativos) {
        // Volatilidade (simplificada)
        if (ativos.size() > 1) {
            BigDecimal somaVariacoes = ativos.stream()
                    .filter(a -> a.getVariacaoPercentual() != null)
                    .map(RentabilidadeResponse::getVariacaoPercentual)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal mediaVariacoes = somaVariacoes.divide(BigDecimal.valueOf(ativos.size()), 4, RoundingMode.HALF_UP);
            
            BigDecimal somaQuadrados = ativos.stream()
                    .filter(a -> a.getVariacaoPercentual() != null)
                    .map(a -> a.getVariacaoPercentual().subtract(mediaVariacoes).pow(2))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal variancia = somaQuadrados.divide(BigDecimal.valueOf(ativos.size() - 1), 4, RoundingMode.HALF_UP);
            BigDecimal volatilidade = new BigDecimal(Math.sqrt(variancia.doubleValue()));
            response.setVolatilidade(volatilidade);
        }
    }

    /**
     * Calcula performance por período
     */
    private void calcularPerformancePorPeriodo(CarteiraRentabilidadeResponse response, Carteira carteira) {
        // Implementação simplificada - em um sistema real, seria necessário histórico de preços
        // Por enquanto, usamos a rentabilidade total como base
        if (response.getRentabilidadePercentualLiquida() != null) {
            response.setRentabilidadeMes(response.getRentabilidadePercentualLiquida().divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP));
            response.setRentabilidadeTrimestre(response.getRentabilidadePercentualLiquida().divide(BigDecimal.valueOf(4), 4, RoundingMode.HALF_UP));
            response.setRentabilidadeSemestre(response.getRentabilidadePercentualLiquida().divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP));
            response.setRentabilidadeAno(response.getRentabilidadePercentualLiquida());
            response.setRentabilidadeYTD(response.getRentabilidadePercentualLiquida());
        }
    }
}
