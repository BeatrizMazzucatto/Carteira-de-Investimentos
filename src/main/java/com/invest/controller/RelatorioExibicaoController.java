package com.invest.controller;

import com.invest.dto.RelatorioExibicaoResponse;
import com.invest.dto.RelatorioEmpresaResponse;
import com.invest.service.RelatorioExibicaoService;
import com.invest.service.RelatorioEmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para relatórios de exibição
 * Retorna JSON completo para uso no front-end
 */
@RestController
@RequestMapping("/api/relatorio")
@CrossOrigin(origins = "*")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios em JSON")
public class RelatorioExibicaoController {

    @Autowired
    private RelatorioExibicaoService relatorioExibicaoService;

    @Autowired
    private RelatorioEmpresaService relatorioEmpresaService;

    @Operation(
        summary = "Gera relatório completo de exibição do investidor",
        description = "Retorna um JSON agregado com todos os dados do investidor para exibição no front-end"
    )
    @GetMapping("/investidor/{investidorId}")
    public ResponseEntity<RelatorioExibicaoResponse> getRelatorioExibicao(
            @PathVariable Long investidorId) {
        try {
            RelatorioExibicaoResponse relatorio = relatorioExibicaoService.gerarRelatorioExibicao(investidorId);
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
        summary = "Gera relatório consolidado da empresa",
        description = "Retorna um JSON consolidado com dados agregados de todos os investidores. " +
                     "Ideal para processamento no front-end e análise empresarial. " +
                     "Inclui estatísticas gerais, resumo por investidor, estatísticas por tipo de ativo e transações recentes."
    )
    @GetMapping("/empresa")
    public ResponseEntity<RelatorioEmpresaResponse> getRelatorioEmpresa() {
        try {
            RelatorioEmpresaResponse relatorio = relatorioEmpresaService.gerarRelatorioEmpresa();
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

