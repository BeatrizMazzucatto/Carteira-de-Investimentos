package com.invest.controller;

import com.invest.dto.TransacaoRequest;
import com.invest.dto.TransacaoResponse;
import com.invest.exception.ResourceNotFoundException;
import com.invest.model.TipoTransacao;
import com.invest.model.Transacao;
import com.invest.service.TransacaoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar transações de investimentos
 * Permite registro de compras, vendas, proventos, etc.
 */
@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Operation(summary = "Lista todas as transações de uma carteira",
               description = "Retorna todas as transações de uma carteira específica, paginadas e ordenadas")
    @GetMapping("/carteira/{carteiraId}")
    public List<TransacaoResponse> getTransacoesByCarteira(
            @PathVariable Long carteiraId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataTransacao") String sort) {
        
        List<Transacao> transacoes = transacaoService.getTransacoesByCarteira(carteiraId);
        
        return transacoes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Busca uma transação específica",
               description = "Retorna os detalhes de uma transação pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponse> getTransacaoById(@PathVariable Long id) {
        try {
            Transacao transacao = transacaoService.getTransacaoById(id);
            TransacaoResponse response = convertToResponse(transacao);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException("Transação não encontrada: " + id);
        }
    }

    @Operation(summary = "Busca transações por tipo",
               description = "Filtra transações de uma carteira pelo tipo (COMPRA, VENDA, PROVENTO)")
    @GetMapping("/carteira/{carteiraId}/tipo/{tipo}")
    public List<TransacaoResponse> getTransacoesByTipo(
            @PathVariable Long carteiraId,
            @PathVariable String tipo) {
        
        try {
            TipoTransacao tipoTransacao = TipoTransacao.valueOf(tipo.toUpperCase());
            List<Transacao> transacoes = transacaoService.getTransacoesByTipo(carteiraId, tipoTransacao);
            
            return transacoes.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de transação inválido: " + tipo);
        }
    }

    @Operation(summary = "Busca transações por ativo",
               description = "Filtra transações de uma carteira pelo código do ativo")
    @GetMapping("/carteira/{carteiraId}/ativo/{codigoAtivo}")
    public List<TransacaoResponse> getTransacoesByAtivo(
            @PathVariable Long carteiraId,
            @PathVariable String codigoAtivo) {
        
        List<Transacao> transacoes = transacaoService.getTransacoesByAtivo(carteiraId, codigoAtivo);
        
        return transacoes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Busca transações por período",
               description = "Filtra transações de uma carteira entre datas de início e fim")
    @GetMapping("/carteira/{carteiraId}/periodo")
    public List<TransacaoResponse> getTransacoesByPeriodo(
            @PathVariable Long carteiraId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        List<Transacao> transacoes = transacaoService.getTransacoesByPeriodo(carteiraId, dataInicio, dataFim);
        
        return transacoes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Cria uma nova transação",
               description = "Registra uma transação (compra, venda ou provento) em uma carteira")
    @PostMapping("/carteira/{carteiraId}")
    public ResponseEntity<TransacaoResponse> createTransacao(
            @PathVariable Long carteiraId,
            @RequestBody @Valid TransacaoRequest transacaoRequest) {
        
        Transacao transacao = transacaoService.createTransacao(carteiraId, transacaoRequest);
        TransacaoResponse response = convertToResponse(transacao);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualiza uma transação",
               description = "Atualiza todos os campos de uma transação pelo ID")
    @PutMapping("/{id}")
    public TransacaoResponse updateTransacao(
            @PathVariable Long id,
            @RequestBody @Valid TransacaoRequest transacaoRequest) {
        
        Transacao transacao = transacaoService.updateTransacao(id, transacaoRequest);
        return convertToResponse(transacao);
    }

    @Operation(summary = "Atualização parcial de uma transação",
               description = "Atualiza parcialmente uma transação pelo ID (PATCH)")
    @PatchMapping("/{id}")
    public TransacaoResponse patchTransacao(
            @PathVariable Long id,
            @RequestBody TransacaoRequest transacaoRequest) {
        
        // Implementar atualização parcial
        Transacao transacao = transacaoService.updateTransacao(id, transacaoRequest);
        return convertToResponse(transacao);
    }

    @Operation(summary = "Deleta uma transação",
               description = "Remove uma transação pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransacao(@PathVariable Long id) {
        transacaoService.deleteTransacao(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtém estatísticas de uma carteira",
               description = "Retorna estatísticas agregadas das transações de uma carteira")
    @GetMapping("/carteira/{carteiraId}/estatisticas")
    public ResponseEntity<TransacaoService.CarteiraStats> getEstatisticasCarteira(@PathVariable Long carteiraId) {
        TransacaoService.CarteiraStats stats = transacaoService.calcularEstatisticasCarteira(carteiraId);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Registra compra de ativo",
               description = "Cria uma transação de compra em uma carteira")
    @PostMapping("/carteira/{carteiraId}/compra")
    public ResponseEntity<TransacaoResponse> registrarCompra(
            @PathVariable Long carteiraId,
            @RequestBody @Valid TransacaoRequest transacaoRequest) {
        
        transacaoRequest.setTipoTransacao(TipoTransacao.COMPRA);
        Transacao transacao = transacaoService.createTransacao(carteiraId, transacaoRequest);
        TransacaoResponse response = convertToResponse(transacao);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Registra venda de ativo",
               description = "Cria uma transação de venda em uma carteira")
    @PostMapping("/carteira/{carteiraId}/venda")
    public ResponseEntity<TransacaoResponse> registrarVenda(
            @PathVariable Long carteiraId,
            @RequestBody @Valid TransacaoRequest transacaoRequest) {
        
        transacaoRequest.setTipoTransacao(TipoTransacao.VENDA);
        Transacao transacao = transacaoService.createTransacao(carteiraId, transacaoRequest);
        TransacaoResponse response = convertToResponse(transacao);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Registra provento de ativo",
               description = "Cria uma transação de recebimento de provento em uma carteira")
    @PostMapping("/carteira/{carteiraId}/provento")
    public ResponseEntity<TransacaoResponse> registrarProvento(
            @PathVariable Long carteiraId,
            @RequestBody @Valid TransacaoRequest transacaoRequest) {
        
        transacaoRequest.setTipoTransacao(TipoTransacao.PROVENTO);
        Transacao transacao = transacaoService.createTransacao(carteiraId, transacaoRequest);
        TransacaoResponse response = convertToResponse(transacao);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Converte entidade Transacao para DTO de resposta
     */
    private TransacaoResponse convertToResponse(Transacao transacao) {
        return new TransacaoResponse(
                transacao.getId(),
                transacao.getTipoTransacao(),
                transacao.getCodigoAtivo(),
                transacao.getNomeAtivo(),
                transacao.getTipoAtivo(),
                transacao.getQuantidade(),
                transacao.getPrecoUnitario(),
                transacao.getValorTotal(),
                transacao.getTaxasCorretagem(),
                transacao.getImpostos(),
                transacao.getValorLiquido(),
                transacao.getDataTransacao(),
                transacao.getDataLiquidacao(),
                transacao.getObservacoes(),
                transacao.getCarteira().getId(),
                transacao.getCarteira().getNome(),
                transacao.getAtivo() != null ? transacao.getAtivo().getId() : null
        );
    }
}
