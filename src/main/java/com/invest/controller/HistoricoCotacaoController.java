package com.invest.controller;

import com.invest.dto.CarteiraHistoricoResponse;
import com.invest.dto.HistoricoCotacaoResponse;
import com.invest.service.CarteiraHistoricoService;
import com.invest.service.HistoricoCotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para histórico de cotações
 * Endpoints para gerar gráficos de variação de ações no front-end
 */
@RestController
@RequestMapping("/api/historico")
@CrossOrigin(origins = "*")
public class HistoricoCotacaoController {

    @Autowired
    private HistoricoCotacaoService historicoCotacaoService;

    @Autowired
    private CarteiraHistoricoService carteiraHistoricoService;

    @Operation(summary = "Busca histórico de um ativo específico",
               description = "Retorna histórico completo de cotações de um ativo para gerar gráficos")
    @GetMapping("/ativo/{codigo}")
    public ResponseEntity<HistoricoCotacaoResponse> getHistoricoAtivo(@PathVariable String codigo) {
        try {
            HistoricoCotacaoResponse historico = historicoCotacaoService.getHistoricoAtivo(codigo);
            if (historico == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Busca histórico de todos os ativos",
               description = "Retorna histórico de todos os ativos disponíveis")
    @GetMapping("/ativos")
    public ResponseEntity<List<HistoricoCotacaoResponse>> getAllHistorico() {
        try {
            List<HistoricoCotacaoResponse> historicos = historicoCotacaoService.getAllHistorico();
            return ResponseEntity.ok(historicos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Busca histórico de uma carteira cruzado com cotações",
               description = "Retorna histórico de uma carteira mostrando variação de cada ativo para gerar gráficos")
    @GetMapping("/carteira/{carteiraId}")
    public ResponseEntity<CarteiraHistoricoResponse> getHistoricoCarteira(@PathVariable Long carteiraId) {
        try {
            CarteiraHistoricoResponse historico = carteiraHistoricoService.getHistoricoCarteira(carteiraId);
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

