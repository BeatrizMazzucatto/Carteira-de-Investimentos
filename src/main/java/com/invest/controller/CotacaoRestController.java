package com.invest.controller;

import com.invest.dto.CotacaoDTO;
import com.invest.service.CotacaoStreamingService;
import com.invest.service.external.GoogleSheetsService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para consulta de cotações em tempo real
 * Permite acesso via HTTP às cotações atualizadas periodicamente
 */
@RestController
@RequestMapping("/api/cotacoes")
@CrossOrigin(origins = "*")
public class CotacaoRestController {

    @Autowired
    private CotacaoStreamingService cotacaoStreamingService;

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @Operation(summary = "Listar todas as cotações",
               description = "Retorna todas as cotações disponíveis no JSON com timestamp e total de ativos")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCotacoes() {
        Map<String, BigDecimal> cotacoesMap = googleSheetsService.getAllCotacoes();
        
        Map<String, Object> response = new HashMap<>();
        response.put("cotacoes", cotacoesMap);
        response.put("total", cotacoesMap.size());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar cotação de um ativo",
               description = "Retorna a cotação de um ativo específico do JSON pelo código do ativo")
    @GetMapping("/{codigo}")
    public ResponseEntity<Map<String, Object>> getCotacao(@PathVariable String codigo) {
        BigDecimal preco = googleSheetsService.buscarPrecoAtivo(codigo);
        
        if (preco == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("codigo", codigo.toUpperCase());
        response.put("preco", preco);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Forçar atualização de cotações",
               description = "Inicia imediatamente a atualização das cotações do serviço de streaming")
    @PostMapping("/atualizar")
    public ResponseEntity<Map<String, String>> forcarAtualizacao() {
        cotacaoStreamingService.forcarAtualizacao();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Atualização de cotações iniciada");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verificar status do serviço de cotações",
               description = "Retorna status do serviço, quantidade de cotações disponíveis e fonte de dados")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "online");
        status.put("cotacoesDisponiveis", googleSheetsService.getAllCotacoes().size());
        status.put("fonte", "cotacoes.json");
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }
}
