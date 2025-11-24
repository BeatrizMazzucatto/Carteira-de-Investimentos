package com.invest.service;

import com.invest.dto.RelatorioEmpresaResponse;
import com.invest.model.*;
import com.invest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service para gerar relatório consolidado da empresa
 * Agrega dados de todos os investidores para processamento no front-end
 */
@Service
@Transactional
public class RelatorioEmpresaService {

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private AtivoRepository ativoRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    /**
     * Gera relatório consolidado da empresa com dados de todos os investidores
     */
    public RelatorioEmpresaResponse gerarRelatorioEmpresa() {
        RelatorioEmpresaResponse relatorio = new RelatorioEmpresaResponse();
        
        // Metadados
        relatorio.setDataGeracao(LocalDateTime.now());
        relatorio.setVersao("1.0");
        
        // Busca todos os investidores
        List<Investidor> todosInvestidores = investidorRepository.findAll();
        relatorio.setTotalInvestidores(todosInvestidores.size());
        
        // Processa cada investidor e agrega dados
        List<RelatorioEmpresaResponse.InvestidorResumo> investidoresResumo = new ArrayList<>();
        BigDecimal valorTotalInvestido = BigDecimal.ZERO;
        BigDecimal valorTotalAtual = BigDecimal.ZERO;
        int totalCarteiras = 0;
        int totalAtivos = 0;
        int totalTransacoes = 0;
        
        Map<String, RelatorioEmpresaResponse.EstatisticaTipoAtivo> estatisticasPorTipo = new HashMap<>();
        List<Transacao> todasTransacoes = new ArrayList<>();
        
        for (Investidor investidor : todosInvestidores) {
            RelatorioEmpresaResponse.InvestidorResumo investidorResumo = processarInvestidor(investidor);
            investidoresResumo.add(investidorResumo);
            
            // Acumula valores totais
            if (investidorResumo.getValorTotalInvestido() != null) {
                valorTotalInvestido = valorTotalInvestido.add(investidorResumo.getValorTotalInvestido());
            }
            if (investidorResumo.getValorTotalAtual() != null) {
                valorTotalAtual = valorTotalAtual.add(investidorResumo.getValorTotalAtual());
            }
            totalCarteiras += investidorResumo.getTotalCarteiras() != null ? investidorResumo.getTotalCarteiras() : 0;
            totalAtivos += investidorResumo.getTotalAtivos() != null ? investidorResumo.getTotalAtivos() : 0;
            totalTransacoes += investidorResumo.getTotalTransacoes() != null ? investidorResumo.getTotalTransacoes() : 0;
            
            // Coleta transações do investidor
            List<Carteira> carteiras = carteiraRepository.findByInvestidor(investidor);
            for (Carteira carteira : carteiras) {
                todasTransacoes.addAll(transacaoRepository.findByCarteira(carteira));
            }
            
            // Agrega estatísticas por tipo de ativo
            agregarEstatisticasPorTipo(investidor, estatisticasPorTipo);
        }
        
        // Calcula rentabilidade das estatísticas por tipo
        for (RelatorioEmpresaResponse.EstatisticaTipoAtivo estat : estatisticasPorTipo.values()) {
            if (estat.getValorTotalInvestido() != null && estat.getValorTotalAtual() != null) {
                BigDecimal rentabilidade = estat.getValorTotalAtual().subtract(estat.getValorTotalInvestido());
                estat.setRentabilidade(rentabilidade);
                
                if (estat.getValorTotalInvestido().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentual = rentabilidade
                        .divide(estat.getValorTotalInvestido(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                    estat.setRentabilidadePercentual(percentual);
                }
            }
        }
        
        // Estatísticas Gerais Consolidadas
        relatorio.setTotalCarteiras(totalCarteiras);
        relatorio.setTotalAtivos(totalAtivos);
        relatorio.setTotalTransacoes(totalTransacoes);
        relatorio.setValorTotalInvestido(valorTotalInvestido);
        relatorio.setValorTotalAtual(valorTotalAtual);
        
        BigDecimal rentabilidadeTotal = valorTotalAtual.subtract(valorTotalInvestido);
        relatorio.setRentabilidadeTotal(rentabilidadeTotal);
        
        if (valorTotalInvestido.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentual = rentabilidadeTotal
                .divide(valorTotalInvestido, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
            relatorio.setRentabilidadePercentual(percentual);
        } else {
            relatorio.setRentabilidadePercentual(BigDecimal.ZERO);
        }
        
        relatorio.setInvestidores(investidoresResumo);
        relatorio.setEstatisticasPorTipo(estatisticasPorTipo);
        
        // Transações Recentes (últimas 20 de todos os investidores)
        todasTransacoes.sort((t1, t2) -> {
            if (t1.getDataTransacao() == null && t2.getDataTransacao() == null) return 0;
            if (t1.getDataTransacao() == null) return 1;
            if (t2.getDataTransacao() == null) return -1;
            return t2.getDataTransacao().compareTo(t1.getDataTransacao()); // Mais recente primeiro
        });
        
        List<RelatorioEmpresaResponse.TransacaoResumo> transacoesRecentes = todasTransacoes.stream()
            .limit(20)
            .map(this::convertToTransacaoResumo)
            .collect(Collectors.toList());
        
        relatorio.setTransacoesRecentes(transacoesRecentes);
        
        return relatorio;
    }
    
    private RelatorioEmpresaResponse.InvestidorResumo processarInvestidor(Investidor investidor) {
        RelatorioEmpresaResponse.InvestidorResumo resumo = new RelatorioEmpresaResponse.InvestidorResumo();
        
        resumo.setId(investidor.getId());
        resumo.setNome(investidor.getNome());
        resumo.setEmail(investidor.getEmail());
        resumo.setDataCriacao(investidor.getDataCriacao());
        
        // Busca carteiras do investidor
        List<Carteira> carteiras = carteiraRepository.findByInvestidor(investidor);
        resumo.setTotalCarteiras(carteiras.size());
        
        BigDecimal valorTotalInvestido = BigDecimal.ZERO;
        BigDecimal valorTotalAtual = BigDecimal.ZERO;
        int totalAtivos = 0;
        int totalTransacoes = 0;
        
        for (Carteira carteira : carteiras) {
            if (carteira.getValorInicial() != null) {
                valorTotalInvestido = valorTotalInvestido.add(carteira.getValorInicial());
            }
            if (carteira.getValorAtual() != null) {
                valorTotalAtual = valorTotalAtual.add(carteira.getValorAtual());
            }
            
            List<Ativo> ativos = ativoRepository.findByCarteira(carteira);
            totalAtivos += ativos.size();
            
            long countTransacoes = transacaoRepository.countByCarteira(carteira);
            totalTransacoes += (int) countTransacoes;
        }
        
        resumo.setValorTotalInvestido(valorTotalInvestido);
        resumo.setValorTotalAtual(valorTotalAtual);
        resumo.setTotalAtivos(totalAtivos);
        resumo.setTotalTransacoes(totalTransacoes);
        
        // Calcula rentabilidade
        BigDecimal rentabilidade = valorTotalAtual.subtract(valorTotalInvestido);
        resumo.setRentabilidade(rentabilidade);
        
        if (valorTotalInvestido.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentual = rentabilidade
                .divide(valorTotalInvestido, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
            resumo.setRentabilidadePercentual(percentual);
        } else {
            resumo.setRentabilidadePercentual(BigDecimal.ZERO);
        }
        
        return resumo;
    }
    
    private void agregarEstatisticasPorTipo(Investidor investidor, 
                                            Map<String, RelatorioEmpresaResponse.EstatisticaTipoAtivo> estatisticasPorTipo) {
        List<Carteira> carteiras = carteiraRepository.findByInvestidor(investidor);
        
        for (Carteira carteira : carteiras) {
            List<Ativo> ativos = ativoRepository.findByCarteira(carteira);
            
            for (Ativo ativo : ativos) {
                    TipoAtivo tipo = ativo.getTipo();
                    if (tipo != null) {
                        String tipoStr = tipo.name(); // Converte enum para String
                        RelatorioEmpresaResponse.EstatisticaTipoAtivo estat = estatisticasPorTipo.getOrDefault(
                            tipoStr, new RelatorioEmpresaResponse.EstatisticaTipoAtivo());
                        estat.setTipo(tipoStr);
                    estat.setQuantidade((estat.getQuantidade() != null ? estat.getQuantidade() : 0) + 1);
                    
                    // Calcula valores
                    if (ativo.getQuantidade() != null && ativo.getPrecoCompra() != null) {
                        BigDecimal valorInvestido = ativo.getQuantidade().multiply(ativo.getPrecoCompra());
                        BigDecimal valorAtual = estat.getValorTotalInvestido() != null ? 
                            estat.getValorTotalInvestido() : BigDecimal.ZERO;
                        estat.setValorTotalInvestido(valorAtual.add(valorInvestido));
                    }
                    
                    if (ativo.getQuantidade() != null && ativo.getPrecoAtual() != null) {
                        BigDecimal valorAtual = ativo.getQuantidade().multiply(ativo.getPrecoAtual());
                        BigDecimal valorAtualTotal = estat.getValorTotalAtual() != null ? 
                            estat.getValorTotalAtual() : BigDecimal.ZERO;
                        estat.setValorTotalAtual(valorAtualTotal.add(valorAtual));
                    }
                    
                        estatisticasPorTipo.put(tipoStr, estat);
                }
            }
        }
    }
    
    private RelatorioEmpresaResponse.TransacaoResumo convertToTransacaoResumo(Transacao transacao) {
        RelatorioEmpresaResponse.TransacaoResumo resumo = new RelatorioEmpresaResponse.TransacaoResumo();
        
        resumo.setId(transacao.getId());
        resumo.setTipoTransacao(transacao.getTipoTransacao() != null ? transacao.getTipoTransacao().name() : null);
        resumo.setCodigoAtivo(transacao.getCodigoAtivo());
        resumo.setNomeAtivo(transacao.getNomeAtivo());
        resumo.setTipoAtivo(transacao.getTipoAtivo() != null ? transacao.getTipoAtivo().name() : null);
        resumo.setQuantidade(transacao.getQuantidade());
        resumo.setPrecoUnitario(transacao.getPrecoUnitario());
        resumo.setValorTotal(transacao.getValorTotal());
        resumo.setDataTransacao(transacao.getDataTransacao());
        
        if (transacao.getCarteira() != null) {
            resumo.setCarteiraId(transacao.getCarteira().getId());
            resumo.setCarteiraNome(transacao.getCarteira().getNome());
            
            if (transacao.getCarteira().getInvestidor() != null) {
                resumo.setInvestidorId(transacao.getCarteira().getInvestidor().getId());
                resumo.setInvestidorNome(transacao.getCarteira().getInvestidor().getNome());
            }
        }
        
        return resumo;
    }
}

