package com.invest.service;

import com.invest.dto.CarteiraHistoricoResponse;
import com.invest.dto.HistoricoCotacaoResponse;
import com.invest.model.Ativo;
import com.invest.model.Carteira;
import com.invest.repository.CarteiraRepository;
import com.invest.service.external.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para cruzar histórico de cotações com carteiras
 * Gera relatórios para gráficos mostrando variação dos ativos nas carteiras
 */
@Service
public class CarteiraHistoricoService {

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private HistoricoCotacaoService historicoCotacaoService;

    @Autowired
    private GoogleSheetsService googleSheetsService;

    /**
     * Retorna histórico de uma carteira cruzado com cotações
     */
    public CarteiraHistoricoResponse getHistoricoCarteira(Long carteiraId) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
            .orElseThrow(() -> new RuntimeException("Carteira não encontrada: " + carteiraId));

        CarteiraHistoricoResponse response = new CarteiraHistoricoResponse();
        response.setCarteiraId(carteira.getId());
        response.setCarteiraNome(carteira.getNome());
        response.setInvestidorNome(carteira.getInvestidor().getNome());

        List<CarteiraHistoricoResponse.AtivoHistorico> ativosHistorico = new ArrayList<>();
        BigDecimal valorTotalInvestido = BigDecimal.ZERO;
        BigDecimal valorTotalAtual = BigDecimal.ZERO;

        // Processa cada ativo da carteira
        for (Ativo ativo : carteira.getAtivos()) {
            CarteiraHistoricoResponse.AtivoHistorico ativoHistorico = 
                processarAtivoHistorico(ativo);
            
            if (ativoHistorico != null) {
                ativosHistorico.add(ativoHistorico);
                valorTotalInvestido = valorTotalInvestido.add(ativoHistorico.getValorInvestido());
                valorTotalAtual = valorTotalAtual.add(ativoHistorico.getValorAtual());
            }
        }

        response.setAtivos(ativosHistorico);

        // Calcula resumo da carteira
        CarteiraHistoricoResponse.ResumoCarteira resumo = new CarteiraHistoricoResponse.ResumoCarteira();
        resumo.setValorTotalInvestido(valorTotalInvestido);
        resumo.setValorTotalAtual(valorTotalAtual);
        
        BigDecimal ganhoPerda = valorTotalAtual.subtract(valorTotalInvestido);
        resumo.setGanhoPerdaTotal(ganhoPerda);
        
        if (valorTotalInvestido.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ganhoPerdaPercentual = ganhoPerda
                .divide(valorTotalInvestido, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
            resumo.setGanhoPerdaPercentualTotal(ganhoPerdaPercentual);
        } else {
            resumo.setGanhoPerdaPercentualTotal(BigDecimal.ZERO);
        }

        response.setResumo(resumo);

        return response;
    }

    /**
     * Processa histórico de um ativo específico
     */
    private CarteiraHistoricoResponse.AtivoHistorico processarAtivoHistorico(Ativo ativo) {
        try {
            // Busca histórico de cotações do ativo
            HistoricoCotacaoResponse historicoCotacao = 
                historicoCotacaoService.getHistoricoAtivo(ativo.getCodigo());

            if (historicoCotacao == null || historicoCotacao.getHistorico() == null) {
                return null;
            }

            CarteiraHistoricoResponse.AtivoHistorico ativoHistorico = 
                new CarteiraHistoricoResponse.AtivoHistorico();
            
            ativoHistorico.setCodigo(ativo.getCodigo());
            ativoHistorico.setNome(ativo.getNome());
            ativoHistorico.setQuantidade(ativo.getQuantidade());
            ativoHistorico.setPrecoMedioCompra(ativo.getPrecoCompra());

            // Preço atual
            BigDecimal precoAtual = googleSheetsService.buscarPrecoAtivo(ativo.getCodigo());
            if (precoAtual == null) {
                precoAtual = ativo.getPrecoAtual();
            }

            // Valores
            BigDecimal valorInvestido = ativo.getPrecoCompra().multiply(ativo.getQuantidade());
            BigDecimal valorAtual = precoAtual.multiply(ativo.getQuantidade());
            BigDecimal ganhoPerda = valorAtual.subtract(valorInvestido);
            BigDecimal ganhoPerdaPercentual = BigDecimal.ZERO;
            
            if (valorInvestido.compareTo(BigDecimal.ZERO) > 0) {
                ganhoPerdaPercentual = ganhoPerda
                    .divide(valorInvestido, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }

            ativoHistorico.setValorAtual(valorAtual);
            ativoHistorico.setValorInvestido(valorInvestido);
            ativoHistorico.setGanhoPerda(ganhoPerda);
            ativoHistorico.setGanhoPerdaPercentual(ganhoPerdaPercentual);

            // Converte histórico de cotações para histórico da carteira
            List<CarteiraHistoricoResponse.HistoricoItem> historico = new ArrayList<>();
            for (HistoricoCotacaoResponse.HistoricoItem item : historicoCotacao.getHistorico()) {
                CarteiraHistoricoResponse.HistoricoItem historicoItem = 
                    new CarteiraHistoricoResponse.HistoricoItem();
                
                historicoItem.setDataHora(item.getDataHora());
                historicoItem.setPreco(item.getPreco());
                
                // Calcula valores totais para este ativo
                BigDecimal valorTotalItem = item.getPreco().multiply(ativo.getQuantidade());
                BigDecimal ganhoPerdaItem = valorTotalItem.subtract(valorInvestido);
                BigDecimal ganhoPerdaPercentualItem = BigDecimal.ZERO;
                
                if (valorInvestido.compareTo(BigDecimal.ZERO) > 0) {
                    ganhoPerdaPercentualItem = ganhoPerdaItem
                        .divide(valorInvestido, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                }

                historicoItem.setValorTotal(valorTotalItem);
                historicoItem.setGanhoPerda(ganhoPerdaItem);
                historicoItem.setGanhoPerdaPercentual(ganhoPerdaPercentualItem);

                historico.add(historicoItem);
            }

            ativoHistorico.setHistorico(historico.toArray(new CarteiraHistoricoResponse.HistoricoItem[0]));

            return ativoHistorico;

        } catch (Exception e) {
            System.err.println("Erro ao processar histórico do ativo " + ativo.getCodigo() + ": " + e.getMessage());
            return null;
        }
    }
}

