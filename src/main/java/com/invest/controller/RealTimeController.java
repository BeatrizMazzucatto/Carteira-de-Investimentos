package com.invest.controller;

import com.invest.service.RealTimeUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para controle de atualizações em tempo real
 * Permite forçar atualizações manualmente
 * 
 * Só é criado quando app.realtime.enabled=true (modo servidor)
 */
@RestController
@RequestMapping("/api/realtime")
@CrossOrigin(origins = "*")
@ConditionalOnProperty(name = "app.realtime.enabled", havingValue = "true", matchIfMissing = false)
public class RealTimeController {

    @Autowired
    private RealTimeUpdateService realTimeUpdateService;

    @Operation(summary = "Forçar atualização de cotações",
               description = "Força atualização imediata de cotações e envia para clientes conectados")
    @PostMapping("/cotacoes/atualizar")
    public ResponseEntity<Map<String, String>> atualizarCotacoes() {
        try {
            realTimeUpdateService.enviarAtualizacaoCotacoes();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("mensagem", "Atualização de cotações iniciada");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Forçar geração de relatório da empresa",
               description = "Força geração imediata do relatório da empresa e envia para clientes conectados")
    @PostMapping("/relatorio/gerar")
    public ResponseEntity<Map<String, String>> gerarRelatorio() {
        try {
            realTimeUpdateService.enviarRelatorioEmpresa();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("mensagem", "Relatório da empresa gerado e enviado");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Status do serviço de tempo real",
               description = "Retorna status do serviço de atualizações em tempo real")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("servico", "RealTimeUpdateService");
        status.put("ativo", true);
        status.put("atualizacaoCotacoes", "A cada 5 minutos");
        status.put("geracaoRelatorio", "A cada 10 minutos");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}

